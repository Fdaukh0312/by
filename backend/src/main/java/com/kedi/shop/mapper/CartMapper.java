package com.kedi.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kedi.shop.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
