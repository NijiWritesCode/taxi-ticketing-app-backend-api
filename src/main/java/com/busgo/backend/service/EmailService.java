package com.busgo.backend.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String fullName);
    void sendOtpEmail(String to, String otpCode);
    void sendTicketEmail(String to, String fullName, String pnr, String details, byte[] qrCode);
    void sendPasswordResetEmail(String toEmail, String name, String token);
}
