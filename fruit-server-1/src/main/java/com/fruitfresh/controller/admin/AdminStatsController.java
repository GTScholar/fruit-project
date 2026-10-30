package com.fruitfresh.controller.admin;

import com.fruitfresh.common.Result;
import com.fruitfresh.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据统计（管理端仪表盘）
 */
@Tag(name = "数据统计(管理端)")
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "核心指标概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(adminStatsService.overview());
    }

    @Operation(summary = "近7天销售趋势")
    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> salesTrend() {
        return Result.success(adminStatsService.salesTrend());
    }

    @Operation(summary = "分类销售占比")
    @GetMapping("/category-sales")
    public Result<List<Map<String, Object>>> categorySales() {
        return Result.success(adminStatsService.categorySales());
    }

    @Operation(summary = "系统运行状态（MySQL/Redis/RabbitMQ 连通性）")
    @GetMapping("/system-status")
    public Result<Map<String, Object>> systemStatus() {
        return Result.success(adminStatsService.systemStatus());
    }
}
