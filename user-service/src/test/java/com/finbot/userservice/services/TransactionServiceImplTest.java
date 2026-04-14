package com.finbot.userservice.services;

import com.finbot.userservice.dto.TransactionEventDto;
import com.finbot.userservice.dto.TransactionRequestDto;
import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.Transaction;
import com.finbot.userservice.entities.TransactionType;
import com.finbot.userservice.entities.User;
import com.finbot.userservice.exception.InvalidDateRangeException;
import com.finbot.userservice.exception.UserNotFoundException;
import com.finbot.userservice.repositories.TransactionRepository;
import com.finbot.userservice.services.impl.TransactionServiceImpl;
import com.finbot.userservice.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private  KafkaProducerService kafkaProducerService;
    @Mock
    private AmqpTemplate amqpTemplate;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Transaction transaction;
    private TransactionRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("omer@finbot.com")
                .password("encodedPassword")
                .fullName("Ömer Akıncı")
                .build();

        requestDto = TransactionRequestDto.builder()
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .category("Market")
                .description("Haftalık alışveriş")
                .transactionDate(LocalDate.now())
                .build();

        transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .user(user)
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .category("Market")
                .description("Haftalık alışveriş")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_ShouldReturnTransactionResponseDto_WhenUserExists() {
        // Arrange
        when(userService.findByUserId(user.getId())).thenReturn(user);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionResponseDto result = transactionService.create(user.getId(), requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(result.getType()).isEqualTo(transaction.getType());
        assertThat(result.getCategory()).isEqualTo(transaction.getCategory());
        verify(transactionRepository).save(any(Transaction.class));
        verify(kafkaProducerService).sendTransactionEvent(any(TransactionEventDto.class));
    }

    @Test
    void create_ShouldReturnTransactionResponseDto_WhenUserNotFound() {
        // Arrange
        when(userService.findByUserId(user.getId())).thenThrow(new UserNotFoundException("Kullanıcı bulunamadı"));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(user.getId(), requestDto))
                .isInstanceOf(UserNotFoundException.class);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(kafkaProducerService, never()).sendTransactionEvent(any(TransactionEventDto.class));
    }

    @Test
    void getTransactionByDateRange_ShouldThrowException_WhenDateRangeInvalid() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(5);
        when(userService.findByUserId(user.getId())).thenReturn(user);

        // Act & Assert
        assertThatThrownBy(() -> transactionService
                .getTransactionsByDateRange(user.getId(), startDate, endDate))
                .isInstanceOf(InvalidDateRangeException.class);
        verify(transactionRepository, never()).findByUserIdAndTransactionDateBetween(any(), any(), any());
    }

    @Test
    void getTransactionByDateRange_ShouldThrowException_WhenDateRangeValid() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        when(userService.findByUserId(user.getId())).thenReturn(user);
        when(transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getId(), startDate, endDate)).thenReturn(List.of(transaction));

        // Act
        List<TransactionResponseDto> result = transactionService
                .getTransactionsByDateRange(user.getId(), startDate, endDate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Market");
    }
}