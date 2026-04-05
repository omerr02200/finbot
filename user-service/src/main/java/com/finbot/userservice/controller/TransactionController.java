package com.finbot.userservice.controller;

import com.finbot.userservice.dto.TransactionRequestDto;
import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.TransactionType;
import com.finbot.userservice.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transaction", description = "Gelir gider işlemleri")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "İşlem kaydı", description = "Yeni işlem kaydı oluştur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Yeni işlem eklendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz İstek")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<TransactionResponseDto> create
            (@PathVariable UUID userId, @Valid @RequestBody TransactionRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(userId, request));
    }

    @Operation(summary = "Kullanıcı Id ile işlemleri getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İşlemler listelendi"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "400", description = "Geçersiz İstek")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Page<TransactionResponseDto>> getUserTransactions(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "transactionDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(transactionService.getUserTransactions(userId, pageable));
    }

    @Operation(summary = "Kullanıcı Id ile, tarih aralığındaki işlemleri getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İşlemler listelendi"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "400", description = "Geçersiz İstek")
    })
    @GetMapping("/{userId}/date-range")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsDateRange(
            @PathVariable UUID userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(userId, startDate, endDate));
    }

    @Operation(summary = "Kullanıcı Id ile, belirtilen tipteki işlemleri getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İşlemler listelendi"),
            @ApiResponse(responseCode = "404", description = "KUllanıcı bulunamadı"),
            @ApiResponse(responseCode = "400", description = "Geçersiz İstek")
    })
    @GetMapping("/{userId}/type")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByType(
            @PathVariable UUID userId,
            @RequestParam TransactionType type) {

        return ResponseEntity.ok(transactionService.getTransactionsByType(userId, type));
    }
}
