package com.busgo.backend.controller;
import com.busgo.backend.dto.PaymentInitiateResponse;
import com.busgo.backend.service.PaymentService;
import com.busgo.backend.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.busgo.backend.repository.PaymentRepository;
import com.busgo.backend.model.Payment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.busgo.backend.service.PaymentGatewayService;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final WebhookService webhookService;
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(@PathVariable Long bookingId, Authentication authentication) {
        return ResponseEntity.ok(paymentService.initiatePayment(bookingId, authentication.getName()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> paystackWebhook(
            @RequestBody String payload, 
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {
        
        if (signature == null || !paymentGatewayService.verifyPaymentWebhook(payload, signature)) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.path("event").asText();
            
            if ("charge.success".equals(event)) {
                String reference = root.path("data").path("reference").asText();
                Payment payment = paymentRepository.findByTransactionId(reference)
                    .orElseThrow(() -> new RuntimeException("Payment not found for reference: " + reference));
                webhookService.handleSuccessfulPayment(payment.getId());
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
}
