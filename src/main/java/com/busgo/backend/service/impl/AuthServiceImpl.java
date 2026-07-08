package com.busgo.backend.service.impl;
import com.busgo.backend.config.JwtUtil;
import com.busgo.backend.dto.*;
import com.busgo.backend.model.User;
import com.busgo.backend.repository.UserRepository;
import com.busgo.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final com.busgo.backend.config.CustomUserDetailsService userDetailsService;
    private final com.busgo.backend.service.EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .kycComplete(false)
                .build();
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        
        return AuthResponse.builder().user(mapToDto(user)).token(token).refreshToken(refreshToken).build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return AuthResponse.builder().user(mapToDto(user)).token(token).refreshToken(refreshToken).build();
    }

    @Override
    public void sendOtp(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("User not found for phone: " + phone));
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setOtpCode(otp);
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public AuthResponse verifyOtp(String phone, String otp) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("User not found for phone: " + phone));
        
        if (user.getOtpCode() == null || !user.getOtpCode().equals(otp) || user.getOtpExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        // Clear OTP after successful use
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return AuthResponse.builder().user(mapToDto(user)).token(token).refreshToken(refreshToken).build();
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        String token = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken()).orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));
        
        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String token = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            User user = userRepository.findByEmail(email).orElseThrow();
            return AuthResponse.builder().user(mapToDto(user)).token(token).refreshToken(newRefreshToken).build();
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .id("usr_" + user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .nextOfKinPhone(user.getNextOfKinPhone())
                .nextOfKinName(user.getNextOfKinName())
                .nin(user.getNin())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .kycComplete(user.getKycComplete())
                .build();
    }
}
