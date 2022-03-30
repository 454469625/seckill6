package com.lucq.seckill.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    //0表示永不过期
    //用public是因为abstract是没办法new的
    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //根据不同的类进行拼接
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
