package com.finbot.userservice.services;

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
import com.finbot.userservice.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("omer@finbot.com")
                .password("encodedPassword")
                .fullName("Ömer Akıncı")
                .build();

        registerRequest = RegisterRequestDto.builder()
                .email("omer@finbot.com")
                .password("123456")
                .fullName("Ömer Akıncı")
                .build();

        loginRequest = LoginRequestDto.builder()
                .email("omer@finbot.com")
                .password("123456")
                .build();
    }

    @Test
    void register_ShouldReturnUserResponseDto_WhenEmailNotExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto result = userServiceImpl.register(registerRequest);

        // Assert
        assertThat(result).isNotNull();

        assertThat(result.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(result.getFullName()).isEqualTo(registerRequest.getFullName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAllreadayExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.register(registerRequest))
                .isInstanceOf(EmailAllreadyExistsException.class)
                .hasMessageContaining(registerRequest.getEmail());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(loginRequest.getEmail())).thenReturn("jwt.token.here");

        // Act
        AuthResponseDto result = userServiceImpl.login(loginRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt.token.here");
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(jwtService).generateToken(loginRequest.getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid () {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.login(loginRequest)).isInstanceOf(InvalidCredentialsException.class);
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        UserResponseDto result = userServiceImpl.getUserById(user.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        // Act && Assert
        assertThatThrownBy(() -> userServiceImpl.getUserById(randomId)).isInstanceOf(UserNotFoundException.class);
    }

}
