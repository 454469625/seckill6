package com.lucq.seckill.service;

import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.SeckillOrder;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.redis.SeckillKey;
import com.lucq.seckill.util.MD5Util;
import com.lucq.seckill.util.UUIDUitl;
import com.lucq.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {

    //本来引入goodsdao不过一般service与dao对应,所以这里改为引入goodsservice
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser seckillUser, GoodsVo goodsVo) {
        boolean success = goodsService.reduceStock(goodsVo);
        if (success) {
            //没有卖完,生成订单,写入order_info和seckillOrder两个表,返回成功
            return orderService.createOrder(seckillUser, goodsVo);
        } else {
            //卖完了,将商品卖完记入缓存,给getSeckillResult方法使用
            setGoodsOver(goodsVo.getId());
            return null;
        }

    }


    public long getSeckillResult(Long seckillUserId, long goodsId) {
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(seckillUserId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            //判断商品是否卖完
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                //todo 在到达这里之前,已经有两次判断stock是否小于0,这里还会被执行吗
                //商品已经卖完,返回-1
                return -1;
            } else {
                //没有卖完,继续轮询(就是生成订单的数据库操作还在队列中,还没处理,需要再查询一次)
                return 0;
            }
        }

    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }

    public boolean checkPath(SeckillUser seckillUser, long goodsId, String path) {
        if (seckillUser == null || path == null) {
            return false;
        }
        String str = redisService.get(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, String.class);
        return path.equals(str);
    }

    public String createSeckillPath(SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.MD5(UUIDUitl.uuid() + "123456");
        redisService.set(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, seckillUser.getId() + "," + goodsId, rnd);
        //输出图片
        return image;

    }

    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(verifyCode);
        } catch (Exception e) {
            System.out.println("calc error");
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String str = "" + num1 + op1 + num2 + op2 + num3;
        return str;
    }

    public boolean checkVerifyCode(SeckillUser seckillUser, long goodsId, int verifyCode) {
        if (seckillUser == null || goodsId <= 0) {
            return false;
        }
        Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode, seckillUser.getId() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(SeckillKey.getSeckillVerifyCode, seckillUser.getId() + "," + goodsId);
        return true;
    }
}
