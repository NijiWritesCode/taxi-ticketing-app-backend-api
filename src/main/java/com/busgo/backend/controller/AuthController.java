package com.busgo.backend.controller;
import com.busgo.backend.dto.AuthResponse;
import com.busgo.backend.dto.LoginRequest;
import com.busgo.backend.dto.RegisterRequest;
import com.busgo.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final com.busgo.backend.service.EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody com.busgo.backend.dto.OtpRequest request) {
        authService.sendOtp(request.getPhone());
        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody com.busgo.backend.dto.VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getPhone(), request.getOtp()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody com.busgo.backend.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset link sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody com.busgo.backend.dto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody com.busgo.backend.dto.RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        return ResponseEntity.ok(emailService.testEmail(email));
    }
}
