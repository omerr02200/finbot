package com.finbot.userservice.controller;

import com.finbot.userservice.dto.RegisterRequestDto;
import com.finbot.userservice.dto.UserResponseDto;
import com.finbot.userservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Kullanıcı işlemleri")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Kullanıcı kaydı", description = "Yeni kullanıcı oluştur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Kullanıcı oluşturuldu"),
            @ApiResponse(responseCode = "409", description = "Email zaten kayıtlı"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek")
    })
    @PostMapping("register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @Operation(summary = "Id ile kullanıcı getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı bulundu"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("id/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Email ile kullanıcı getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı bulundu"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
