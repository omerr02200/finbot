package com.finbot.analyticsservice.services;

import com.finbot.analyticsservice.dto.TransactionEventDto;
import com.finbot.analyticsservice.entities.SpendingSummary;
import com.finbot.analyticsservice.repositories.SpendingSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpendingAnalyticsService {

    private final SpendingSummaryRepository spendingSummaryRepository;

    public void processTransactionEvent(TransactionEventDto event) {

        LocalDate periodStart = event.getTransactionDate().withDayOfMonth(1);
        LocalDate periodEnd = event.getTransactionDate()
                .withDayOfMonth(event.getTransactionDate().lengthOfMonth());

        Optional<SpendingSummary> existing = spendingSummaryRepository
                .findByUserIdAndCategoryAndAndPeriodStartAndPeriodEnd(
                event.getUserId(), event.getCategory(), periodStart, periodEnd
        );

        if(existing.isPresent()) {
            SpendingSummary summary = existing.get();
            summary.setTotalAmount(summary.getTotalAmount().add(event.getAmount()));
            summary.setTransactionCount(summary.getTransactionCount() + 1);
            spendingSummaryRepository.save(summary);
            log.info("Özet güncellendi: userId={}, category={}",  summary.getUserId(), summary.getCategory());
        } else {
            SpendingSummary summary = SpendingSummary.builder()
                    .userId(event.getUserId())
                    .category(event.getCategory())
                    .totalAmount(event.getAmount())
                    .transactionCount(1)
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .build();
            spendingSummaryRepository.save(summary);
            log.info("Yeni özet oluşturuldu: userId={}, category={}",  event.getUserId(), event.getCategory());
        }
    }
}