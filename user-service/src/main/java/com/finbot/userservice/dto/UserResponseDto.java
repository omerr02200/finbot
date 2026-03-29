package com.finbot.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.finbot.userservice.entities.User}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto implements Serializable {
    private UUID id;
    @NotNull(message = "E-mail alanı boş olamaz")
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}