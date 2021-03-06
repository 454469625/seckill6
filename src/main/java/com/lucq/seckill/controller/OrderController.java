package com.lucq.seckill.controller;


import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.domain.UserInfo;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.result.CodeMsg;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.GoodsService;
import com.lucq.seckill.service.OrderService;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.vo.GoodsVo;
import com.lucq.seckill.vo.OrderDetailVo;
import com.lucq.seckill.vo.OrdersVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    SeckillUserService seckillUserService;


    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> orderDetail(Model model, SeckillUser user,
                                             @RequestParam("orderId") long orderId) {
        if (user == null) {
//            System.out.println("用户不存在");
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setUserInfo(new UserInfo(user.getNickname(), user.getId()));
        orderDetailVo.setGoodsVo(goodsVo);
        orderDetailVo.setOrderInfo(orderInfo);
        return Result.success(orderDetailVo);
    }

    @RequestMapping(value = "/to_list")
    public String list(Model model, SeckillUser seckillUser) {

        model.addAttribute("user", seckillUser);
        List<OrdersVo> ordersList = orderService.listOrdersVo(seckillUser.getId());
        model.addAttribute("ordersList", ordersList);
        return "ordersList";
    }
}
