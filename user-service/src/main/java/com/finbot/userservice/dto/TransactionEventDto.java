package com.finbot.userservice.dto;

import com.finbot.userservice.entities.TransactionType;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEventDto implements Serializable {

    private UUID transactionId;
    private UUID userId;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate transactionDate;
}
