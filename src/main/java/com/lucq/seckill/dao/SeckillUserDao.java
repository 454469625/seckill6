package com.lucq.seckill.dao;

import com.lucq.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SeckillUserDao {

    @Select("select * from seckilluser where id = #{id}")
    public SeckillUser getById(@Param("id") long id);

    @Update("update seckilluser set password = #{password} where id = #{id}")
    void update(SeckillUser toBeUpdate);

    @Insert("insert into seckilluser(nickname, id, password, salt, register_date) values ( #{nickname}, #{id}, #{password}, '1a2b3c4d', NOW())")
    void insert(@Param("id") long id, @Param("nickname") String nickname, @Param("password") String password);
}
