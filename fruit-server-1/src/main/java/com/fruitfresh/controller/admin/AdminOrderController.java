package com.fruitfresh.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.entity.Orders;
import com.fruitfresh.service.OrderService;
import com.fruitfresh.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理（管理端）
 */
@Tag(name = "订单管理(管理端)")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "订单分页查询")
    @GetMapping("/page")
    public Result<Page<Orders>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String orderNo,
                                     @RequestParam(required = false) Integer status) {
        return Result.success(orderService.adminPage(pageNum, pageSize, orderNo, status));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.adminDetail(id));
    }

    @Operation(summary = "订单发货")
    @PutMapping("/{id}/deliver")
    public Result<Void> deliver(@PathVariable Long id) {
        orderService.deliver(id);
        return Result.success();
    }
}
