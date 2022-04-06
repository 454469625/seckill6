package com.lucq.seckill.service;

import com.lucq.seckill.dao.SeckillUserDao;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.exception.GlobalException;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.redis.SeckillUserKey;
import com.lucq.seckill.result.CodeMsg;
import com.lucq.seckill.util.MD5Util;
import com.lucq.seckill.util.UUIDUitl;
import com.lucq.seckill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {
    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";


    public SeckillUser getById(long id) {
        //对象缓存,将user放入redis中不用每次都查数据库
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
        if (user != null) {
            return user;
        }
        user = seckillUserDao.getById(id);
        if (user != null) {
            redisService.set(SeckillUserKey.getById, "" + id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass) {
        SeckillUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //新建对象是因为可以根据需要赋值修改对应的属性,不用原来的就不会进行全更新,提高性能
        //更新数据库
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        //先写数据库
        seckillUserDao.update(toBeUpdate);
        //然后删除对象缓存
        redisService.delete(SeckillUserKey.getById, "" + id);
        //更新token
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(SeckillUserKey.token, token, user);
        return true;
    }

    /**
     * 登录功能待优化,每次都是登录产生新cookie,并没有根据cookie自动登录功能
     * @param response
     * @param loginVo
     * @return
     */
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            System.out.println("loginvo == null");
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        SeckillUser seckillUser = getById(Long.parseLong(mobile));
        if (seckillUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbPass = seckillUser.getPassword();
        String saltDB = seckillUser.getSalt();
        //将前端过来的密码(已经MD5一次了)与数据库salt进行MD5
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        //匹配二次加密的前端密码是否与数据库存储密码相等
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUitl.uuid();
        addCookie(response, token, seckillUser);
        return true;

    }


    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser seckillUser = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
        if (seckillUser != null) {
            addCookie(response, token, seckillUser);
        }
        return seckillUser;
    }

    private void addCookie(HttpServletResponse response, String token, SeckillUser seckillUser) {
        //将token对应的用户信息写入redis
        redisService.set(SeckillUserKey.token, token, seckillUser);
        //再将token写入cookie传给客户端
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置过期时间与redis上的信息一致
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void register(String mobile, String nickname, String password) {
        String calcPass = MD5Util.formPassToDBPass(password, "1a2b3c4d");
        seckillUserDao.insert(Long.parseLong(mobile), nickname, calcPass);
    }

    public void adminLogin(HttpServletResponse response, LoginVo loginVo) {
        if (StringUtils.isEmpty(loginVo.getMobile()) || StringUtils.isEmpty(loginVo.getPassword()) || !loginVo.getMobile().equals("13923914118") || !loginVo.getPassword().equals(MD5Util.inputPassToFormPass("123456"))) {

            throw new GlobalException(CodeMsg.ADMIN_ERROR);
        }
    }
}
