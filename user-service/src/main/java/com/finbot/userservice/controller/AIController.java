package com.finbot.userservice.controller;

import com.finbot.userservice.services.AIService;
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
public class AIController {

    private final AIService aiService;

    @GetMapping("advice/{userId}")
    public ResponseEntity<String> advice(@PathVariable UUID userId) {
        return ResponseEntity.ok(aiService.getSpendingAdvice(userId));
    }
}
