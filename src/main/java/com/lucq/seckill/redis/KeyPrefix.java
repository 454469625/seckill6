package com.lucq.seckill.redis;

public interface KeyPrefix {
    int expireSeconds();

    String getPrefix();
}
