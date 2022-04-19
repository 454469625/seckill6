package com.lucq.seckill.dao;

import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.SeckillOrder;
import com.lucq.seckill.vo.GoodsVo;
import com.lucq.seckill.vo.OrderDetailVo;
import com.lucq.seckill.vo.OrdersVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price from seckill_goods sg left join goods g on sg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("SELECT o.id, g.goods_name, g.goods_img, o.create_date FROM order_info o, goods g WHERE o.user_id = #{userId} and o.goods_id = g.id")
    List<OrdersVo> listOrdersVo(@Param("userId") long userId);

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price " +
            "from seckill_goods sg left join goods g on sg.goods_id = g.id" +
            " where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Select("select * from seckill_order where user_id = #{userId} and goods_id = #{goodsId} ")
    SeckillOrder getOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into " +
            "order_info(user_id,goods_id,goods_name,goods_count," +
            "goods_price,order_channel,status,create_date) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsCount}," +
            "#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id",resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);


    @Insert("insert into seckill_order(user_id,goods_id,order_id) " +
            "values(#{userId},#{goodsId},#{orderId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderByOrderId(@Param("orderId") long orderId);
}
