package com.fruitfresh.service;

import java.util.List;
import java.util.Map;

/**
 * 管理端数据统计服务接口（仪表盘）
 */
public interface AdminStatsService {

    /** 核心指标：用户数 / 商品数 / 订单数 / 今日订单 / 销售总额 */
    Map<String, Object> overview();

    /** 近7天每日销售额趋势 */
    List<Map<String, Object>> salesTrend();

    /** 分类销售占比 */
    List<Map<String, Object>> categorySales();

    /** 系统运行状态：MySQL / Redis / RabbitMQ 连通性探测 */
    Map<String, Object> systemStatus();
}
