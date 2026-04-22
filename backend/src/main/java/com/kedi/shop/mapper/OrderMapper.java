package com.kedi.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kedi.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE deleted = 0 AND status = 4 AND DATE(create_time) = CURDATE()")
    BigDecimal selectTodaySales();

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE deleted = 0 AND status = 4")
    BigDecimal selectTotalSales();
}
