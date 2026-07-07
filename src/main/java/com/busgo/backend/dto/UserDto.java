package com.busgo.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private BigDecimal walletBalance;
}
