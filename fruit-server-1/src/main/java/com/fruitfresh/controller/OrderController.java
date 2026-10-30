package com.fruitfresh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.dto.OrderCreateDTO;
import com.fruitfresh.entity.Orders;
import com.fruitfresh.security.SecurityUtil;
import com.fruitfresh.service.OrderService;
import com.fruitfresh.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口（用户端）
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单（购物车结算）")
    @PostMapping("/create")
    public Result<Orders> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.create(SecurityUtil.getCurrentUserId(), dto));
    }

    @Operation(summary = "我的订单分页（status: 0待付款 1待发货 2待收货 3已完成 4已取消）")
    @GetMapping("/list")
    public Result<Page<Orders>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) Integer status) {
        return Result.success(orderService.list(SecurityUtil.getCurrentUserId(), pageNum, pageSize, status));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(SecurityUtil.getCurrentUserId(), id));
    }

    @Operation(summary = "模拟支付")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.pay(SecurityUtil.getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(SecurityUtil.getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(SecurityUtil.getCurrentUserId(), id);
        return Result.success();
    }
}
