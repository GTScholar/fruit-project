package com.fruitfresh.controller;

import com.fruitfresh.common.Result;
import com.fruitfresh.dto.CartAddDTO;
import com.fruitfresh.security.SecurityUtil;
import com.fruitfresh.service.CartService;
import com.fruitfresh.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车接口
 */
@Tag(name = "购物车接口")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "查询我的购物车")
    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        return Result.success(cartService.list(SecurityUtil.getCurrentUserId()));
    }

    @Operation(summary = "加入购物车")
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(SecurityUtil.getCurrentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "修改商品数量")
    @PutMapping("/quantity/{cartId}")
    public Result<Void> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
        cartService.updateQuantity(SecurityUtil.getCurrentUserId(), cartId, quantity);
        return Result.success();
    }

    @Operation(summary = "勾选/取消勾选")
    @PutMapping("/checked/{cartId}")
    public Result<Void> updateChecked(@PathVariable Long cartId, @RequestParam Integer checked) {
        cartService.updateChecked(SecurityUtil.getCurrentUserId(), cartId, checked);
        return Result.success();
    }

    @Operation(summary = "删除购物车记录")
    @DeleteMapping("/{cartId}")
    public Result<Void> delete(@PathVariable Long cartId) {
        cartService.delete(SecurityUtil.getCurrentUserId(), cartId);
        return Result.success();
    }
}
