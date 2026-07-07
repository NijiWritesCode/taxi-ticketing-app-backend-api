package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AuthResponse {
    private UserDto user;
    private String token;
    private String refreshToken;
}
