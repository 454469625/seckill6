package com.lucq.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     *
     * @param keyPrefix 传进来的是一个keyPrefix的实现类,调用的是BasePrefix的getPrefix()方法
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //将前缀与key拼接成真正的key
            //getPrefix是BasePrefix的方法,生成字符串"UserKey:"
            String realKey = keyPrefix.getPrefix() + key;
            String s = jedis.get(realKey);
            T t = stringToBean(s, clazz);
            return t;
        } finally {
            returnToPoll(jedis);
        }

    }

    public <T> boolean set(KeyPrefix keyPrefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            String realKey = keyPrefix.getPrefix() + key;
            int seconds = keyPrefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            returnToPoll(jedis);
        }
    }

    public <T> boolean exists(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPoll(jedis);
        }
    }

    public boolean delete(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        } finally {
            returnToPoll(jedis);
        }
    }

    public <T> Long incr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPoll(jedis);
        }
    }

    public <T> Long decr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPoll(jedis);
        }
    }

    public static <T> String beanToString(T value) {
        if (value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        //如果是int类,long类,String类,直接转换
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String)value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;

        } else {
            //如果是自定义的类,将对象转换为字符串
            return JSON.toJSONString(value);
        }

    }

    /**
     *
     * @param s
     * @param clazz 只支持clazz类型是bean类型,不支持list等集合
     * @param <T> 要返回的类型,根据参数clazz决定
     * @return
     */
    public static <T> T stringToBean(String s, Class<T> clazz) {
        if (s == null || s.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(s);
        } else if (clazz == String.class) {
            return (T)s;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T)Long.valueOf(s);

        } else {
            //将字符串转换为object,然后强转为clazz类型
            return JSON.toJavaObject(JSON.parseObject(s), clazz);
        }
    }

    private void returnToPoll(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }

    }


}
