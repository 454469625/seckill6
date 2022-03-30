package com.lucq.seckill.vo;

import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.UserInfo;

public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo orderInfo;
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
