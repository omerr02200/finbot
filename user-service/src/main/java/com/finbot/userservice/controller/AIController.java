package com.finbot.userservice.controller;

import com.finbot.userservice.services.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "Yapay zeka destekli harcama analizi")
public class AIController {

    private final AIService aiService;

    @Operation(summary = "Harcama önerisi al", description = "Kullanıcının bu ayki harcamalarına göre AI destekli tasarruf önerileri sunar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Öneri başarıyla alındı"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("advice/{userId}")
    public ResponseEntity<String> advice(@PathVariable UUID userId) {
        return ResponseEntity.ok(aiService.getSpendingAdvice(userId));
    }
}
