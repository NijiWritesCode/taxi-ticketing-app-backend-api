package com.busgo.backend.dto;
import lombok.Data;
@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
