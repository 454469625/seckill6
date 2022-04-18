package com.lucq.seckill.vo;

import java.util.Date;

public class OrdersVo {
    private String goodsName;
    private String goodsImg;
    private Date createDate;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public OrdersVo(String goodsName, String goodsImg, Date createDate) {
        this.goodsName = goodsName;
        this.goodsImg = goodsImg;
        this.createDate = createDate;
    }
}
