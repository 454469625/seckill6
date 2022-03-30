package com.lucq.seckill.dao;

import com.lucq.seckill.domain.Goods;
import com.lucq.seckill.domain.SeckillGoods;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}
