package com.busgo.backend.service;
import com.busgo.backend.dto.AuthResponse;
import com.busgo.backend.dto.LoginRequest;
import com.busgo.backend.dto.RegisterRequest;
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void sendOtp(String phone);
    AuthResponse verifyOtp(String phone, String otp);
    void forgotPassword(String email);
}
