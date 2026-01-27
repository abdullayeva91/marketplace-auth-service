package com.marketplace.marketplaceauthservice;

import com.marketplace.marketplaceauthservice.config.JwtTokenProvider;
import com.marketplace.marketplaceauthservice.enums.Role;
import com.marketplace.marketplaceauthservice.model.User;
import com.marketplace.marketplaceauthservice.repository.UserRepository;
import com.marketplace.marketplaceauthservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_Success() {
        when(userRepository.existsUserByUsername("Lale")).thenReturn(false);
        when(userRepository.findByEmail("lale@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded_123");

        User mockUser = new User();
        mockUser.setUsername("Lale");
        mockUser.setEmail("lale@mail.com");

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User savedUser = authService.registerUser("Lale", "123", "lale@mail.com", Role.USER);

        assertNotNull(savedUser);
        assertEquals("Lale", savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsException_WhenUsernameExists() {
        when(userRepository.existsUserByUsername("Lale")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser("Lale", "123", "lale@mail.com", Role.USER);
        });
    }

    @Test
    void login_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Lale");
        mockUser.setPassword("encoded_123");
        mockUser.setRole(Role.USER);

        when(userRepository.findByUsername("Lale")).thenReturn(mockUser);
        when(passwordEncoder.matches("123", "encoded_123")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), anyLong())).thenReturn("mock_token");

        String token = authService.login("Lale", "123");

        assertEquals("mock_token", token);
    }

    @Test
    void login_ThrowsException_WhenPasswordInvalid() {
        User mockUser = new User();
        mockUser.setUsername("Lale");
        mockUser.setPassword("encoded_123");

        when(userRepository.findByUsername("Lale")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrong_pass", "encoded_123")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("Lale", "wrong_pass");
        });
    }
}