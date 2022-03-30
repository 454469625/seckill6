package com.lucq.seckill.redis;

public class UserKey extends BasePrefix {
    private UserKey(String prefix) {
        super(prefix);
    }

    //返回的是一个Userkey对象,这样RedisService方法调用时是UserKey.getPrefix()
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");


}
