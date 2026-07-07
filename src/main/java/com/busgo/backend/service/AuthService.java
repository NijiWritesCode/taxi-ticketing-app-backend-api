package com.busgo.backend.service;

import com.busgo.backend.dto.AuthRequest;
import com.busgo.backend.dto.AuthResponse;
import com.busgo.backend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
