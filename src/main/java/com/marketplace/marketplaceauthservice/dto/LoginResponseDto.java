package com.marketplace.marketplaceauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;

    public LoginResponseDto(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}