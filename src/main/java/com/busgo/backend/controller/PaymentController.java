package com.busgo.backend.controller;
import com.busgo.backend.dto.PaymentInitiateResponse;
import com.busgo.backend.service.PaymentService;
import com.busgo.backend.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final WebhookService webhookService;

    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(@PathVariable Long bookingId, Authentication authentication) {
        return ResponseEntity.ok(paymentService.initiatePayment(bookingId, authentication.getName()));
    }

    @PostMapping("/webhook/{paymentId}")
    public ResponseEntity<String> mockWebhook(@PathVariable Long paymentId) {
        return ResponseEntity.ok(webhookService.handleSuccessfulPayment(paymentId));
    }
}
