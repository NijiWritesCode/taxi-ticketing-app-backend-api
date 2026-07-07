package com.busgo.backend.controller;
import com.busgo.backend.dto.UserDto;
import com.busgo.backend.model.User;
import com.busgo.backend.repository.UserRepository;
import com.busgo.backend.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(AuthServiceImpl.mapToDto(user));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto updates, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        if (updates.getNextOfKinPhone() != null) user.setNextOfKinPhone(updates.getNextOfKinPhone());
        if (updates.getNextOfKinName() != null) user.setNextOfKinName(updates.getNextOfKinName());
        if (updates.getNin() != null) user.setNin(updates.getNin());
        if (updates.getFullName() != null) user.setFullName(updates.getFullName());
        userRepository.save(user);
        return ResponseEntity.ok(AuthServiceImpl.mapToDto(user));
    }
}
