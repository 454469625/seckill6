package com.lucq.seckill.service;

import com.lucq.seckill.dao.GoodsDao;
import com.lucq.seckill.domain.Goods;
import com.lucq.seckill.domain.SeckillGoods;
import com.lucq.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
