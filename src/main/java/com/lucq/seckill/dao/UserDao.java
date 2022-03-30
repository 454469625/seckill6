package com.lucq.seckill.dao;

import com.lucq.seckill.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);


    @Insert("insert into demoUser(id, name) values (#{id}, #{name})")
    int insert(User demoUser);
}
