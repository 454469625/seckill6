package com.lucq.seckill.redis;

public class AccessKey extends BasePrefix {
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //页面缓存,设置有效期60s,不宜太长.
    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds, "access");
    }


}
