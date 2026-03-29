package com.finbot.userservice.services;

import com.finbot.userservice.dto.TransactionRequestDto;
import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponseDto create(UUID userId, TransactionRequestDto request);

    Page<TransactionResponseDto> getUserTransactions(UUID userId, Pageable pageable);

    List<TransactionResponseDto> getTransactionsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    List<TransactionResponseDto> getTransactionsByType(UUID userId, TransactionType type);

}