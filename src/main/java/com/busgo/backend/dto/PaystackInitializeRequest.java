package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaystackInitializeRequest {
    private String email;
    private String amount;
    private String reference;
}
