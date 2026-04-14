package com.finbot.userservice.services.impl;

import com.finbot.userservice.config.JWTService;
import com.finbot.userservice.dto.AuthResponseDto;
import com.finbot.userservice.dto.LoginRequestDto;
import com.finbot.userservice.dto.RegisterRequestDto;
import com.finbot.userservice.dto.UserResponseDto;
import com.finbot.userservice.entities.User;
import com.finbot.userservice.exception.EmailAllreadyExistsException;
import com.finbot.userservice.exception.InvalidCredentialsException;
import com.finbot.userservice.exception.UserNotFoundException;
import com.finbot.userservice.repositories.UserRepository;
import com.finbot.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    @Override
    @CacheEvict(value = "users", key = "#result.id.toString()")
    public UserResponseDto register(RegisterRequestDto request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAllreadyExistsException("Bu email zaten kayıtlı:" + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id.toString()")
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Kullanıcı bulunamadı" + id));
        return toResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("Kullanıcı bulunamdı" + email));
        return toResponseDto(user);
    }

    @Override
    public User findByUserId(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı " + id));
        return user;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + request.getEmail()));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email veya şifre hatalı");
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}