package com.fruitfresh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruitfresh.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
