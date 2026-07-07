package com.busgo.backend.service.impl;

import com.busgo.backend.service.PaymentGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Profile("!prod")
public class MockPaymentGatewayService implements PaymentGatewayService {

    @Override
    public String initiatePayment(String bookingId, BigDecimal amount) {
        String checkoutUrl = "https://mock-payment-gateway.com/checkout/" + UUID.randomUUID().toString();
        log.info("[MOCK PAYMENT] Initiating payment for Booking ID {}, Amount: {}. URL: {}", bookingId, amount, checkoutUrl);
        return checkoutUrl;
    }

    @Override
    public boolean verifyPaymentWebhook(String payload, String signature) {
        log.info("[MOCK PAYMENT] Verifying webhook signature: {}", signature);
        return true; // Always true for mock
    }
}
