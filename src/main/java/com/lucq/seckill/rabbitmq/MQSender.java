package com.lucq.seckill.rabbitmq;

import com.lucq.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//被controller或者service调用,所以是service
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    public void sendSeckillMessage(SeckillMessage seckillMessage) {
        String msg = RedisService.beanToString(seckillMessage);
        log.info("send message" + msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }



//    public void send(Object message) {
//        //方法在RedisService中,其实是个通用的方法,为了省事直接将方法设置为public
//        String msg = RedisService.beanToString(message);
//        log.info("send message" + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//    public void sendTopic(Object message) {
//        //方法在RedisService中,其实是个通用的方法,为了省事直接将方法设置为public
//        String msg = RedisService.beanToString(message);
//        log.info("send topic message" + msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
//    }
//
//    public void sendFanout(Object message) {
//        //方法在RedisService中,其实是个通用的方法,为了省事直接将方法设置为public
//        String msg = RedisService.beanToString(message);
//        log.info("send fanout message" + msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
//    }
//
//    public void sendHeaders(Object message) {
//        //方法在RedisService中,其实是个通用的方法,为了省事直接将方法设置为public
//        String msg = RedisService.beanToString(message);
//        log.info("send header message" + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1", "value1");
//        properties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//    }


}
