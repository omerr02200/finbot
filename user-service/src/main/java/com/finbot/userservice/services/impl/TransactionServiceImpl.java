package com.finbot.userservice.services.impl;

import com.finbot.userservice.dto.TransactionRequestDto;
import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.Transaction;
import com.finbot.userservice.entities.TransactionType;
import com.finbot.userservice.entities.User;
import com.finbot.userservice.exception.UserNotFoundException;
import com.finbot.userservice.repositories.TransactionRepository;
import com.finbot.userservice.repositories.UserRepository;
import com.finbot.userservice.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionResponseDto create(UUID userId, TransactionRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow( () -> new UserNotFoundException("Kullanıcı bulunumadı " + userId));

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return toResponseDto(saved);
    }

    @Override
    public Page<TransactionResponseDto> getUserTransactions(UUID userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    public List<TransactionResponseDto> getTransactionsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {

        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDto> getTransactionsByType(UUID userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private TransactionResponseDto toResponseDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
