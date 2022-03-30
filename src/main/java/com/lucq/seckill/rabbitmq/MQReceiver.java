package com.lucq.seckill.rabbitmq;

import com.lucq.seckill.domain.SeckillOrder;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.result.CodeMsg;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.GoodsService;
import com.lucq.seckill.service.OrderService;
import com.lucq.seckill.service.SeckillService;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    //配置监听的队列
    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        log.info("receive message" + message);
        //将消息转换为bean
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
        //获取消息中的用户id和商品id
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        //这个goodsVo是从数据库里面拿的,所以以这个的库存为准
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        //todo controller中已经判断stock是否小于0,这里还要再判断吗
        if (stock <= 0) {
            return;
        }

        //todo 同样的,controller已经判断是否有order,这里还要再判断吗
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsVo.getId());
        if (order != null) {
            return;
        }

        seckillService.seckill(seckillUser, goodsVo);

    }

//    //配置监听的队列
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        log.info("receive message" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        log.info("topic queue1 message" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        log.info("topic queue2 message" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeadersQueue(byte[] message){
//        log.info("header queue message String : " + new String(message));
//    }
}
