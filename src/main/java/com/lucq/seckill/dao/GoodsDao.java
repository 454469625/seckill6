package com.lucq.seckill.dao;

import com.lucq.seckill.domain.Goods;
import com.lucq.seckill.domain.SeckillGoods;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.*;

import javax.validation.constraints.Size;
import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price from seckill_goods sg left join goods g on sg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price " +
            "from seckill_goods sg left join goods g on sg.goods_id = g.id" +
            " where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(SeckillGoods g);

    @Insert("insert into " +
            "goods(goods_name,goods_title,goods_img,goods_detail," +
            "goods_price,goods_stock) " +
            "values(#{goodsName},#{goodsTitle},#{goodsImg},#{goodsDetail}," +
            "#{goodsPrice},#{goodsStock})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insertIntoGoods(Goods goods);

    @Insert("insert into seckill_goods(goods_id, seckill_price, stock_count, start_date, end_date) values(#{goodsId}, #{seckillPrice},#{stockCount},#{startDate},#{endDate})")
    void insertIntoSeckillGoods(SeckillGoods seckillGoods);























}
