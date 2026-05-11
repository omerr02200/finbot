package com.finbot.userservice.services;

import com.finbot.userservice.dto.TransactionResponseDto;
import com.finbot.userservice.entities.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final TransactionService transactionService;
    private final GeminiService geminiService;

    private UserService userService;

    public String getSpendingAdvice(UUID userId) {
        userService.findByUserId(userId);

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<TransactionResponseDto> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate);

        if(transactions.isEmpty())
        {
            return "Bu ay henüz işlem bulunmuyor";
        }

        BigDecimal totalExpense = transactions.stream()
                .filter(t->t.getType() == TransactionType.EXPENSE)
                .map(TransactionResponseDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = transactions.stream()
                .filter(t->t.getType() == TransactionType.INCOME)
                .map(TransactionResponseDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .filter(t->t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        TransactionResponseDto::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                TransactionResponseDto::getAmount,
                                BigDecimal::add)));

        StringBuilder prompt =  new StringBuilder();
        prompt.append("Sen bir kişisel finans danışmanısın . ");
        prompt.append("Kullanıcının bu ayki harcama verilerine göre kısa ve pratik Türkçe öneriler sun.\n\n");
        prompt.append("Toplam gelir: ").append(totalIncome).append(" TL\n");
        prompt.append("Toplam gider: ").append(totalExpense).append(" TL\n");
        prompt.append("Kategori bazlı harcamalar:\n");

        categoryTotals.forEach((category, amount) ->
                prompt.append("- ").append(category).append(":").append(amount).append(" TL/n"));

        prompt.append("Lütfen 3-5 madde halinde kısa tasarruf önerileri sun.");

        return geminiService.getAdvice(prompt.toString());
    }
}