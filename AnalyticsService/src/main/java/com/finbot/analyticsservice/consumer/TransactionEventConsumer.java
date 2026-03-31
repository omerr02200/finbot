package com.finbot.analyticsservice.consumer;

import com.finbot.analyticsservice.dto.TransactionEventDto;
import com.finbot.analyticsservice.services.SpendingAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final SpendingAnalyticsService spendingAnalyticsService;

    @KafkaListener(
            topics = "transaction-events",
            groupId = "analytics-group"
    )
    public void consume(TransactionEventDto event) {
        log.info("Event alındı: transactionId={}, userId={}, category={}",
                event.getTransactionId(), event.getUserId(), event.getCategory());

        spendingAnalyticsService.processTransactionEvent(event);
    }
}
