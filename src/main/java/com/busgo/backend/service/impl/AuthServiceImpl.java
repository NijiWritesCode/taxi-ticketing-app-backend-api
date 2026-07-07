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
        return AuthResponse.builder().user(mapToDto(user)).token(token).build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().user(mapToDto(user)).token(token).build();
    }

    @Override
    public void sendOtp(String phone) {
        // Mock OTP send
    }

    @Override
    public AuthResponse verifyOtp(String phone, String otp) {
        User user = userRepository.findByEmail("adebayo@email.com").orElseGet(() -> {
            User u = User.builder().email("adebayo@email.com").fullName("Adebayo").phone(phone).password(passwordEncoder.encode("123")).role("USER").build();
            return userRepository.save(u);
        });
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().user(mapToDto(user)).token(token).build();
    }

    @Override
    public void forgotPassword(String email) {
        // Mock forgot password
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
