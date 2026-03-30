package com.finbot.userservice.services;

import com.finbot.userservice.dto.TransactionEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionEventDto> kafkaTemplate;

    public void sendTransactionEvent(TransactionEventDto event) {
        kafkaTemplate.send("transaction-events", event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if(ex != null) {
                        log.error("Event gönderilemedi: {}", ex.getMessage());
                    } else {
                        log.info("Event gönderildi : topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
