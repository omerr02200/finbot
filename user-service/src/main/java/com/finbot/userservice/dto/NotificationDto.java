package com.finbot.userservice.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto implements Serializable {

    private UUID userId;
    private String category;
    private BigDecimal totalAmount;
    private BigDecimal limitAmount;
    private String message;
}
