package com.finbot.notificationservice.consumer;

import com.finbot.notificationservice.config.RabbitMQConfig;
import com.finbot.notificationservice.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consume(NotificationDto notification) {
        log.warn("Limit aşıldı! userId={}, category={}, harcanan={}, limit={}",
                notification.getUserId(),
                notification.getCategory(),
                notification.getTotalAmount(),
                notification.getLimitAmount());
        log.info("Bildirim: {}", notification.getMessage());
    }
}