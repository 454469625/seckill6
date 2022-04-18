package com.lucq.seckill.service;

import com.lucq.seckill.dao.OrderDao;
import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.SeckillOrder;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.redis.OrderKey;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.vo.GoodsVo;
import com.lucq.seckill.vo.OrdersVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public List<GoodsVo> listGoodsVo(){
        return orderDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return orderDao.getGoodsVoByGoodsId(goodsId);
    }

    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
        //return orderDao.getOrderByUserIdGoodsId(userId, goodsId);
        //直接从缓存中获取
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(SeckillUser seckillUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(1L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(seckillUser.getId());

        orderDao.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        //@SelectKey会把插入新记录的id封装到orderInfo中,直接拿就行了,如果拿返回值只是拿到插入记录的条数,也就是1
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(seckillUser.getId());
        //此处在数据库中设置了userid+goodsid的索引,如果有同一用户多次请求一个商品的秒杀
        //这个唯一索引可以保证只有一个请求能完成秒杀(重复的索引项无法插入)
        orderDao.insertSeckillOrder(seckillOrder);

        //每次新增订单的时候,都把订单加入缓存,这样判断用户是否已经下单就不用查询数据库,提高一点性能
        redisService.set(OrderKey.getSeckillOrderByUidGid,
                "" + seckillUser.getId() + "_" + goodsVo.getId(), seckillOrder);

        return orderInfo;
    }

    public OrderInfo getOrderByOrderId(long orderId) {
        return orderDao.getOrderByOrderId(orderId);
    }

    public List<OrdersVo> listOrdersVo(long userId) {
        return orderDao.listOrdersVo(userId);
    }
}
