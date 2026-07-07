package com.busgo.backend.service;

import com.busgo.backend.dto.UserDto;

public interface UserService {
    UserDto getCurrentUser(String email);
    UserDto updateCurrentUser(String email, UserDto userDto);
}
