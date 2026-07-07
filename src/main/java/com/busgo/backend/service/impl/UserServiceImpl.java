package com.busgo.backend.service.impl;
import com.busgo.backend.dto.UserDto;
import com.busgo.backend.model.User;
import com.busgo.backend.repository.UserRepository;
import com.busgo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return AuthServiceImpl.mapToDto(user);
    }

    @Override
    public UserDto updateUserProfile(String email, UserDto userDto) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (userDto.getFullName() != null) user.setFullName(userDto.getFullName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        userRepository.save(user);
        return AuthServiceImpl.mapToDto(user);
    }
}
