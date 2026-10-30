package com.fruitfresh.mq;

import com.fruitfresh.config.RabbitMQConfig;
import com.fruitfresh.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单超时消费者：监听死信队列，取消超时未支付的订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_TIMEOUT_QUEUE)
    public void handleOrderTimeout(String orderNo) {
        log.info("收到订单超时消息: {}", orderNo);
        orderService.timeoutCancel(orderNo);
    }
}
