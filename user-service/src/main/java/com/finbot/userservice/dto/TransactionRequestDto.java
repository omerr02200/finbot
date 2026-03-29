package com.finbot.userservice.dto;

import com.finbot.userservice.entities.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link com.finbot.userservice.entities.Transaction}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto implements Serializable {
    @NotNull(message = "Miktar boş olamaz")
    @Positive(message = "Miktar pozitif olmalıdır")
    private BigDecimal amount;
    @NotNull(message = "İşlem tipi boş olamaz")
    private TransactionType type;
    @NotBlank(message = "Kategori boş olamaz")
    private String category;
    @Size(message = "açıklama en fazla 500 karakter olabilir", max = 500)
    private String description;
    @NotNull(message = "İşlem tarihi boş olamaz")
    private LocalDate transactionDate;
}