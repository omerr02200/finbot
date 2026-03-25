package com.finbot.userservice.services;

import com.finbot.userservice.dto.RegisterRequestDto;
import com.finbot.userservice.dto.UserResponseDto;
import com.finbot.userservice.entities.User;
import com.finbot.userservice.exception.EmailAlreadyExistsException;
import com.finbot.userservice.exception.UserNotFoundException;
import com.finbot.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Bu email zaten kayıtlı:" + request.getEmail());
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

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}