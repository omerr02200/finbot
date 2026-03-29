package com.finbot.userservice.dto;

import com.finbot.userservice.entities.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.finbot.userservice.entities.Transaction}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto implements Serializable {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;
}