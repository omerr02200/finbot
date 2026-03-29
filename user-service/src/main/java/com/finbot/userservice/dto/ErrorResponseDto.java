package com.finbot.userservice.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto implements Serializable {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}