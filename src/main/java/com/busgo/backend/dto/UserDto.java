package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String nextOfKinPhone;
    private String nextOfKinName;
    private String nin;
    private String dateOfBirth;
    private String gender;
    private Boolean kycComplete;
}
