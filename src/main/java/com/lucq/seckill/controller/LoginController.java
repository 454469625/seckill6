package com.lucq.seckill.controller;

import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    @ResponseBody
    public Result<Integer> toLogin(@RequestParam String password) {
        return Result.success(0);
    }

    @RequestMapping("/login2")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> dbGet(HttpServletResponse response, @Validated LoginVo loginVo) {
        seckillUserService.login(response, loginVo);
        return Result.success(true);
    }

    @RequestMapping("/to_adminLogin")
    @ResponseBody
    public Result<Integer> toAdminLogin(@RequestParam String password) {
        return Result.success(0);
    }

    @RequestMapping("/adminlogin2")
    public String toAdminLogin() {
        return "adminlogin";
    }

    @RequestMapping("/do_adminlogin")
    @ResponseBody
    public Result<Boolean> doAdminLogin(HttpServletResponse response, @Validated LoginVo loginVo) {
        seckillUserService.adminLogin(response, loginVo);
        return Result.success(true);
    }


}
