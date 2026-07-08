package com.busgo.backend.dto;
import lombok.Data;
@Data
public class VerifyOtpRequest {
    private String phone;
    private String otp;
}
