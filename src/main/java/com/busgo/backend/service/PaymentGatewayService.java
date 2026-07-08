package com.busgo.backend.service;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String initiatePayment(String transactionId, BigDecimal amount, String email);
    boolean verifyPaymentWebhook(String payload, String signature);
}
