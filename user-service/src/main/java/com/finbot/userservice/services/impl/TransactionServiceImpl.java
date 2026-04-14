package com.finbot.userservice.services.impl;

import com.finbot.userservice.config.RabbitMQConfig;
import com.finbot.userservice.dto.*;
import com.finbot.userservice.entities.Transaction;
import com.finbot.userservice.entities.TransactionType;
import com.finbot.userservice.entities.User;
import com.finbot.userservice.exception.InvalidDateRangeException;
import com.finbot.userservice.repositories.TransactionRepository;
import com.finbot.userservice.services.KafkaProducerService;
import com.finbot.userservice.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    // private final UserRepository userRepository;
    private final UserServiceImpl userService;

    private final KafkaProducerService kafkaProducerService;

    private static final BigDecimal MONTHLY_LIMIT = new  BigDecimal("5000");

    private final AmqpTemplate  amqpTemplate;

    @Override
    @CacheEvict(value = "userTransactions", key = "#userId")
    public TransactionResponseDto create(UUID userId, TransactionRequestDto request) {

//        User user = userRepository.findById(userId)
//                .orElseThrow( () -> new UserNotFoundException("Kullanıcı bulunumadı " + userId));
        User user = userService.findByUserId(userId);

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Kafka'ya event gönder
        TransactionEventDto event = TransactionEventDto.builder()
                .transactionId(saved.getId())
                .userId(saved.getUser().getId())
                .amount(saved.getAmount())
                .type(saved.getType())
                .category(saved.getCategory())
                .transactionDate(saved.getTransactionDate())
                .build();
        kafkaProducerService.sendTransactionEvent(event);

        // Limit kontrolü
        if(request.getType() == TransactionType.EXPENSE) {
            checkMonthlyLimit(userId, request);
        }

        return toResponseDto(saved);
    }

    private void checkMonthlyLimit(UUID userId, TransactionRequestDto request) {

        log.info("Limit kontrolü başladı: userId={}", userId);

        LocalDate now = LocalDate.now();
        LocalDate periodStart = now.withDayOfMonth(1);
        LocalDate periodEnd = now.withDayOfMonth(now.lengthOfMonth());

        List<TransactionResponseDto> monthlyTransactions = getTransactionsByDateRange(userId, periodStart, periodEnd);

        log.info("Bu ayki işelm sayısı: {}", monthlyTransactions.size());

        BigDecimal totalExpense = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(TransactionResponseDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Toplam gider: {}", totalExpense);
        log.info("Limit: {}", MONTHLY_LIMIT);
        log.info("Limit aşıldı mı: {}", totalExpense.compareTo(MONTHLY_LIMIT));

        if(totalExpense.compareTo(MONTHLY_LIMIT) > 0) {
            NotificationDto notification = NotificationDto.builder()
                    .userId(userId)
                    .category(request.getCategory())
                    .totalAmount(totalExpense)
                    .limitAmount(MONTHLY_LIMIT)
                    .message(String.format(
                            "Aylık harcama lmitiniz aşıldı! Harcanan: %.2f₺, Limit: %.2f₺",
                            totalExpense, MONTHLY_LIMIT))
                    .build();

            amqpTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    notification);

            log.info("RabbbitMQ'ya bildirim gönderildi: userId={}", userId);
        }
    }

    @Override
    public Page<TransactionResponseDto> getUserTransactions(UUID userId, Pageable pageable) {

        userService.findByUserId(userId);

        return transactionRepository.findByUserId(userId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Cacheable(value = "userTransactions", key = "#userId + '_' + #startDate + '_' + #endDate")
    public List<TransactionResponseDto> getTransactionsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        userService.findByUserId(userId);

        if(startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Başlangıç tarihi, bitiş tarihinden büyük olamaz");
        }

        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "userTransactions", key = "#userId + '_' + #type")
    public List<TransactionResponseDto> getTransactionsByType(UUID userId, TransactionType type) {

        userService.findByUserId(userId);

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
