package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class PaymentInitiateResponse {
    private Long paymentId;
    private String checkoutUrl;
    private String status;
}
