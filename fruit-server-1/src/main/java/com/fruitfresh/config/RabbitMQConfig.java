package com.fruitfresh.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：订单超时未支付自动取消（延迟队列方案）
 * <p>
 * 原理：
 * 1. 下单后消息发送到 延迟交换机 -> 延迟队列（带 30 分钟 TTL，消息过期后进入死信）
 * 2. 死信交换机把过期消息路由到 超时队列
 * 3. 超时消费者监听超时队列，把仍处于"待付款"的订单置为"已取消"
 */
@Configuration
public class RabbitMQConfig {

    /** 延迟交换机 */
    public static final String ORDER_DELAY_EXCHANGE = "fruit.order.delay.exchange";
    /** 延迟队列 */
    public static final String ORDER_DELAY_QUEUE = "fruit.order.delay.queue";
    /** 延迟路由键 */
    public static final String ORDER_DELAY_ROUTING_KEY = "fruit.order.delay";

    /** 死信/业务交换机 */
    public static final String ORDER_DIRECT_EXCHANGE = "fruit.order.direct.exchange";
    /** 超时队列 */
    public static final String ORDER_TIMEOUT_QUEUE = "fruit.order.timeout.queue";
    /** 超时路由键 */
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "fruit.order.timeout";

    /** 订单超时时间（分钟），统一由 application.yml 的 fruit.order.timeout-minutes 控制 */
    @Value("${fruit.order.timeout-minutes:30}")
    private long timeoutMinutes;

    /** 延迟交换机 */
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE, true, false);
    }

    /** 业务交换机 */
    @Bean
    public DirectExchange orderDirectExchange() {
        return new DirectExchange(ORDER_DIRECT_EXCHANGE, true, false);
    }

    /**
     * 延迟队列：消息进入后等待 TTL 过期，过期后投递到死信交换机
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .ttl((int) (timeoutMinutes * 60 * 1000L))
                .deadLetterExchange(ORDER_DIRECT_EXCHANGE)
                .deadLetterRoutingKey(ORDER_TIMEOUT_ROUTING_KEY)
                .build();
    }

    /** 超时队列 */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE).build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with(ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue())
                .to(orderDirectExchange())
                .with(ORDER_TIMEOUT_ROUTING_KEY);
    }
}
