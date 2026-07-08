package com.busgo.backend.service.impl;

import com.busgo.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String fullName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            String htmlBody = templateEngine.process("welcome-email", context);
            sendHtmlEmail(to, "Welcome to Bruzo!", htmlBody);
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
            sendHtmlEmail(to, "Your Bruzo Verification Code", htmlBody);
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

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Your Bruzo E-Ticket: " + pnr);
            helper.setText(htmlBody, true);
            helper.setFrom("bruzo.noreply@gmail.com");

            if (qrCode != null) {
                helper.addAttachment("Bruzo_Ticket_" + pnr + ".png", new ByteArrayResource(qrCode));
            }

            mailSender.send(message);
            log.info("Ticket Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send ticket email", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom("bruzo.noreply@gmail.com");
        mailSender.send(message);
        log.info("Email sent successfully to {}", to);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("token", token);
            String htmlBody = templateEngine.process("password-reset", context);
            sendHtmlEmail(toEmail, "Bruzo - Password Reset Request", htmlBody);
        } catch (Exception e) {
            log.error("Failed to send Password Reset email to {}", toEmail, e);
        }
    }

    @Override
    public String testEmail(String to) {
        try {
            sendHtmlEmail(to, "Bruzo Diagnostic Test", "<p>If you see this, your SMTP connection from Railway is working perfectly!</p>");
            return "SUCCESS: Email sent without errors!";
        } catch (Exception e) {
            String error = e.getMessage();
            if (e.getCause() != null) {
                error += " | Cause: " + e.getCause().getMessage();
            }
            return "FAILED: " + error;
        }
    }
}
