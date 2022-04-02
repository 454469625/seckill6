package com.lucq.seckill.service;

import com.lucq.seckill.dao.GoodsDao;
import com.lucq.seckill.domain.Goods;
import com.lucq.seckill.domain.SeckillGoods;
import com.lucq.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goodsVo) {
        SeckillGoods g = new SeckillGoods();
        g.setGoodsId(goodsVo.getId());
        //reduceStock返回的是更新的行数,所以有更新ret就大于0, 否则如果库存小于等于0,ret就为0
        int ret = goodsDao.reduceStock(g);
        return ret > 0;

    }

    @Transactional
    public long insert(String goodsName, String goodsTitle, String goodsImg, String goodsDetail, Double goodsPrice, Double seckillPrice, Integer stock, Date startDate, Date endDate) {
        Goods goods = new Goods();
        goods.setGoodsName(goodsName);
        goods.setGoodsTitle(goodsTitle);
        goods.setGoodsImg(goodsImg);
        goods.setGoodsDetail(goodsDetail);
        goods.setGoodsPrice(goodsPrice);
        goods.setGoodsStock(stock);

        goodsDao.insertIntoGoods(goods);

        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        seckillGoods.setSeckillPrice(seckillPrice);
        seckillGoods.setStockCount(stock);
        seckillGoods.setStartDate(startDate);
        seckillGoods.setEndDate(endDate);

        goodsDao.insertIntoSeckillGoods(seckillGoods);
        return goods.getId();


    }
}
