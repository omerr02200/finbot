package com.finbot.userservice.controller;

import com.finbot.userservice.dto.TransactionRequestDto;
import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.TransactionType;
import com.finbot.userservice.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{userId}")
    public ResponseEntity<TransactionResponseDto> create
            (@PathVariable UUID userId, @Valid @RequestBody TransactionRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<TransactionResponseDto>> getUserTransactions(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "transactionDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(transactionService.getUserTransactions(userId, pageable));
    }

    @GetMapping("/{userId}/date-range")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsDateRange(
            @PathVariable UUID userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(userId, startDate, endDate));
    }

    @GetMapping("/{userId}/type")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByType(
            @PathVariable UUID userId,
            @RequestParam TransactionType type) {

        return ResponseEntity.ok(transactionService.getTransactionsByType(userId, type));
    }
}
