package com.finbot.userservice.services;

import com.finbot.userservice.dto.RegisterRequestDto;
import com.finbot.userservice.dto.UserResponseDto;
import com.finbot.userservice.entities.User;

import java.util.UUID;

public interface UserService {

    UserResponseDto register(RegisterRequestDto request);

    UserResponseDto getUserById(UUID id);

    UserResponseDto getUserByEmail(String email);

    User findByUserId(UUID id);
}
