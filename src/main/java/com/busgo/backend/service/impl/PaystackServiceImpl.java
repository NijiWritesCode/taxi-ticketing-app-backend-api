package com.busgo.backend.service.impl;

import com.busgo.backend.dto.PaystackInitializeRequest;
import com.busgo.backend.dto.PaystackInitializeResponse;
import com.busgo.backend.service.PaymentGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class PaystackServiceImpl implements PaymentGatewayService {

    @Value("${paystack.secret-key}")
    private String secretKey;

    private final String PAYSTACK_INITIALIZE_URL = "https://api.paystack.co/transaction/initialize";

    @Override
    public String initiatePayment(String transactionId, BigDecimal amount, String email) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + secretKey);
        headers.set("Content-Type", "application/json");

        // Paystack expects amount in Kobo (Naira * 100)
        String amountInKobo = String.valueOf(amount.multiply(new BigDecimal(100)).longValue());

        PaystackInitializeRequest request = PaystackInitializeRequest.builder()
                .email(email)
                .amount(amountInKobo)
                .reference(transactionId)
                .build();

        HttpEntity<PaystackInitializeRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PaystackInitializeResponse> response = restTemplate.exchange(
                    PAYSTACK_INITIALIZE_URL,
                    HttpMethod.POST,
                    entity,
                    PaystackInitializeResponse.class
            );

            if (response.getBody() != null && response.getBody().isStatus()) {
                log.info("[PAYSTACK] Successfully initialized transaction: {}", transactionId);
                return response.getBody().getData().getAuthorization_url();
            } else {
                throw new RuntimeException("Paystack error: " + (response.getBody() != null ? response.getBody().getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("[PAYSTACK] Failed to initialize payment", e);
            throw new RuntimeException("Failed to initiate payment gateway", e);
        }
    }

    @Override
    public boolean verifyPaymentWebhook(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            
            return hashString.toString().equals(signature);
        } catch (Exception e) {
            log.error("[PAYSTACK] Signature verification failed", e);
            return false;
        }
    }
}
