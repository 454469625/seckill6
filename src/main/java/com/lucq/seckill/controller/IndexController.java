package com.lucq.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by jiangyunxiong on 2018/5/21.
 */
@Controller
public class IndexController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/index")
    public String toIndex() {
        return "index";
    }

}
