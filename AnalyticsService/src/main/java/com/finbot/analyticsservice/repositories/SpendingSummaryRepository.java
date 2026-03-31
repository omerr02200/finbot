package com.finbot.analyticsservice.repositories;

import com.finbot.analyticsservice.entities.SpendingSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendingSummaryRepository extends JpaRepository<SpendingSummary, UUID> {

    Optional<SpendingSummary> findByUserIdAndCategoryAndAndPeriodStartAndPeriodEnd(
            UUID userId, String category, LocalDate periodStart, LocalDate periodEnd
    );

    List<SpendingSummary> findByUserIdAndPeriodStartAndPeriodEnd(
            UUID userId, LocalDate periodStart, LocalDate periodEnd
    );

}