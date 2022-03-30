package com.lucq.seckill.controller;

import com.lucq.seckill.domain.User;
import com.lucq.seckill.rabbitmq.MQSender;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.redis.UserKey;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "lu");
        return "hello";
    }
/*
    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        sender.send("hello world message queue");
        return Result.success("mq success");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
        sender.sendTopic("hello world message topic queue");
        return Result.success("mq success");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        sender.sendFanout("hello world message topic queue");
        return Result.success("mq success");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
        sender.sendHeaders("hello world message header queue");
        return Result.success("mq success");
    }
*/
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User demoUser = userService.getUserById(1);
        return Result.success(demoUser);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

//    @RequestMapping("/redis/get/{key}")
//    @ResponseBody
//    public Result<String> redisGet(@PathVariable("key") String key){
//        String v1 = redisService.get(key, String.class);
//        return Result.success(v1);

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User demoUser = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(demoUser);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User demoUser = new User();
        demoUser.setId(1);
        demoUser.setName("1111");
        Boolean res = redisService.set(UserKey.getById, "" + 1, demoUser);
        return Result.success(res);
    }


}
