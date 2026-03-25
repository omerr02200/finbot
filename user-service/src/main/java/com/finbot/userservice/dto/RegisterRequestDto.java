package com.finbot.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto implements Serializable {
    @NotNull(message = "E-mail alanı boş olamaz")
    @NotBlank(message = "Email boş olamaz")
    String email;
    @NotNull(message = "şifre alanı boş olamaz")
    @Size(message = "Şifre en az 6 karakter olmalı", min = 6)
    @NotBlank(message = "Şifre boş olamaz")
    String password;
    @NotBlank(message = "Ad soyad boş olamaz")
    String fullName;
}