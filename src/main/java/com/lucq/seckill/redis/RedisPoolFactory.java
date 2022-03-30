package com.lucq.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//不加@Service,RedisService获取不到JedisPool
@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    /**
     * 读取redisconfig配置生成JedisPool并注入到bean中,这样redisService中就可以用了
     * @return
     */
    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        //配置时用的是秒,所以这里乘1000
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() *1000);
        JedisPool jp = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout());
        return jp;
    }
}
