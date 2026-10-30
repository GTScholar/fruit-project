package com.fruitfresh.service;

import com.fruitfresh.dto.CartAddDTO;
import com.fruitfresh.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /** 查询我的购物车（含商品信息） */
    List<CartVO> list(Long userId);

    /** 加入购物车：已存在则累加数量 */
    void add(Long userId, CartAddDTO dto);

    /** 修改数量 */
    void updateQuantity(Long userId, Long cartId, Integer quantity);

    /** 勾选/取消勾选 */
    void updateChecked(Long userId, Long cartId, Integer checked);

    /** 删除购物车记录 */
    void delete(Long userId, Long cartId);

    /** 清空已勾选商品（下单成功后调用） */
    void clearChecked(Long userId);
}
