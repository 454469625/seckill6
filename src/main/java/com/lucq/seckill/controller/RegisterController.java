package com.lucq.seckill.controller;



import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.validator.IsMobile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;


/**
 * Created by jiangyunxiong on 2018/5/21.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @PostMapping("/to_register")
    @ResponseBody
    public Result<Integer> toRegister(@RequestParam String password) {
        return Result.success(0);
    }

    @RequestMapping("/to_register2")
    public String toRegister2() {
        return "register2";
    }


    @RequestMapping("/do_register")
    @ResponseBody
    public Result<Integer> doRegister(@RequestParam String mobile, @RequestParam String nickname, @RequestParam String password) {//加入JSR303参数校验
        seckillUserService.register(mobile, nickname, password);
        return Result.success(0);
    }

}
