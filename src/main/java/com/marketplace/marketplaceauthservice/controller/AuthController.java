package com.marketplace.marketplaceauthservice.controller;

import com.marketplace.marketplaceauthservice.dto.LoginRequestDto;
import com.marketplace.marketplaceauthservice.dto.LoginResponseDto;
import com.marketplace.marketplaceauthservice.dto.RegisterRequestDto;
import com.marketplace.marketplaceauthservice.dto.UserResponseDto;
import com.marketplace.marketplaceauthservice.model.User;
import com.marketplace.marketplaceauthservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        try {
            User user = authService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getRole()
            );

            UserResponseDto response = new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        try {
            String token = authService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            User user = authService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            LoginResponseDto response = new LoginResponseDto();
            response.setToken(token);
            response.setType("Bearer");
            response.setUsername(user.getUsername());
            response.setRole(user.getRole().name());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.username")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            UserResponseDto response = new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = authService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            UserResponseDto response = new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}