package com.finbot.notificationservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private UUID userId;
    private String category;
    private BigDecimal totalAmount;
    private BigDecimal limitAmount;
    private String message;
}
