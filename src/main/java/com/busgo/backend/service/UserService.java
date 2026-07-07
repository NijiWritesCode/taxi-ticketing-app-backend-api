package com.busgo.backend.service;
import com.busgo.backend.dto.UserDto;
public interface UserService {
    UserDto getUserProfile(String email);
    UserDto updateUserProfile(String email, UserDto userDto);
}
