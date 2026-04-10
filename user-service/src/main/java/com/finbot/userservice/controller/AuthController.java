package com.finbot.userservice.controller;

import com.finbot.userservice.dto.AuthResponseDto;
import com.finbot.userservice.dto.LoginRequestDto;
import com.finbot.userservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Kimlik Doğrulama İşlemleri")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Giriş Yap", description = "Email ve şifre ile giriş yapıp JWT token al")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Giriş Bşşarılı"),
            @ApiResponse(responseCode = "401", description = "Email veya şifre hatalı"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
