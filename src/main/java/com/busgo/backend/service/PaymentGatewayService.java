package com.busgo.backend.service;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String initiatePayment(String bookingId, BigDecimal amount);
    boolean verifyPaymentWebhook(String payload, String signature);
}
