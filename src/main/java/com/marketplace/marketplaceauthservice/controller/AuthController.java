package com.marketplace.marketplaceauthservice.controller;

import com.marketplace.marketplaceauthservice.dto.LoginRequestDto;
import com.marketplace.marketplaceauthservice.dto.RegisterRequestDto;
import com.marketplace.marketplaceauthservice.model.User;
import com.marketplace.marketplaceauthservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto request) {
        try {
            User user = authService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginRequestDto request) {

        try {
            boolean user = authService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.ok().build();

        }

    }

}