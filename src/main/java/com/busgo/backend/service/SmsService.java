package com.busgo.backend.service;

public interface SmsService {
    void sendOtp(String phoneNumber, String otp);
    void sendTicketSms(String phoneNumber, String pnr, String link);
}
