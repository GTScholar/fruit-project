package com.fruitfresh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.CartAddDTO;
import com.fruitfresh.entity.Cart;
import com.fruitfresh.entity.Product;
import com.fruitfresh.enums.ProductStatus;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.CartMapper;
import com.fruitfresh.mapper.ProductMapper;
import com.fruitfresh.service.CartService;
import com.fruitfresh.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    /** 查询我的购物车（含商品信息） */
    @Override
    public List<CartVO> list(Long userId) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getUpdateTime));
        List<CartVO> voList = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null) {
                continue;
            }
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setProductId(product.getId());
            vo.setName(product.getName());
            vo.setImage(product.getImage());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setChecked(cart.getChecked());
            vo.setStock(product.getStock());
            vo.setStatus(product.getStatus());
            voList.add(vo);
        }
        return voList;
    }

    /** 加入购物车：已存在则累加数量 */
    @Override
    public void add(Long userId, CartAddDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || !ProductStatus.ON_SHELF.is(product.getStatus())) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }
        Cart exist = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, dto.getProductId()));
        if (exist != null) {
            int newQuantity = exist.getQuantity() + dto.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new BusinessException("购物车数量超过库存，请减少数量");
            }
            exist.setQuantity(newQuantity);
            cartMapper.updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(dto.getProductId());
            cart.setQuantity(dto.getQuantity());
            cart.setChecked(YesNo.YES.getCode());
            cartMapper.insert(cart);
        }
    }

    /** 修改数量 */
    @Override
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BusinessException("数量至少为1");
        }
        Cart cart = getOwnedCart(userId, cartId);
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (quantity > product.getStock()) {
            throw new BusinessException("商品库存不足");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    /** 勾选/取消勾选 */
    @Override
    public void updateChecked(Long userId, Long cartId, Integer checked) {
        Cart cart = getOwnedCart(userId, cartId);
        cart.setChecked(checked);
        cartMapper.updateById(cart);
    }

    /** 删除购物车记录 */
    @Override
    public void delete(Long userId, Long cartId) {
        Cart cart = getOwnedCart(userId, cartId);
        cartMapper.deleteById(cart.getId());
    }

    /** 清空已勾选商品（下单成功后调用） */
    @Override
    public void clearChecked(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getChecked, YesNo.YES.getCode()));
    }

    /** 校验购物车记录归属 */
    private Cart getOwnedCart(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }
        return cart;
    }
}
