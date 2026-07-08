package com.busgo.backend.service.impl;

import com.busgo.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    private final TemplateEngine templateEngine;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    @Override
    public void sendWelcomeEmail(String to, String fullName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            String htmlBody = templateEngine.process("welcome-email", context);
            sendHtmlEmail(to, "Welcome to Bruzo!", htmlBody, null);
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    @Async
    @Override
    public void sendOtpEmail(String to, String otpCode) {
        try {
            Context context = new Context();
            context.setVariable("otpCode", otpCode);
            String htmlBody = templateEngine.process("otp-email", context);
            sendHtmlEmail(to, "Your Bruzo Verification Code", htmlBody, null);
        } catch (Exception e) {
            log.error("Failed to send OTP email", e);
        }
    }

    @Async
    @Override
    public void sendTicketEmail(String to, String fullName, String pnr, String details, byte[] qrCode) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("pnr", pnr);
            context.setVariable("details", details);
            String htmlBody = templateEngine.process("ticket-email", context);

            Map<String, Object> attachment = null;
            if (qrCode != null) {
                attachment = new HashMap<>();
                attachment.put("name", "Bruzo_Ticket_" + pnr + ".png");
                attachment.put("content", Base64.getEncoder().encodeToString(qrCode));
            }

            sendHtmlEmail(to, "Your Bruzo E-Ticket: " + pnr, htmlBody, attachment);
        } catch (Exception e) {
            log.error("Failed to send ticket email", e);
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("token", token);
            String htmlBody = templateEngine.process("password-reset", context);
            sendHtmlEmail(toEmail, "Bruzo - Password Reset Request", htmlBody, null);
        } catch (Exception e) {
            log.error("Failed to send Password Reset email to {}", toEmail, e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody, Map<String, Object> attachment) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "Bruzo", "email", "bruzo.noreply@gmail.com"));
        body.put("to", Collections.singletonList(Map.of("email", to)));
        body.put("subject", subject);
        body.put("htmlContent", htmlBody);

        if (attachment != null) {
            body.put("attachment", Collections.singletonList(attachment));
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Brevo API error: " + response.getBody());
        }
        log.info("Email sent successfully to {}", to);
    }

    @Override
    public String testEmail(String to) {
        try {
            sendHtmlEmail(to, "Bruzo Diagnostic Test", "<p>If you see this, your Brevo HTTP API connection from Railway is working perfectly!</p>", null);
            return "SUCCESS: Email sent without errors via Brevo API!";
        } catch (Exception e) {
            String error = e.getMessage();
            if (e.getCause() != null) {
                error += " | Cause: " + e.getCause().getMessage();
            }
            return "FAILED: " + error;
        }
    }
}
