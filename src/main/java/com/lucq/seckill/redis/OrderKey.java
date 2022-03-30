package com.lucq.seckill.redis;

public class OrderKey extends BasePrefix {
    private OrderKey(String prefix) {
        super(prefix);
    }

    //页面缓存,设置有效期60s,不宜太长.
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("soUidGid");


}
