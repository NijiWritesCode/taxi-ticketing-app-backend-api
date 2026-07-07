package com.busgo.backend.service.impl;

import com.busgo.backend.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!prod")
public class MockSmsService implements SmsService {

    @Override
    public void sendOtp(String phoneNumber, String otp) {
        log.info("[MOCK SMS] Sending OTP {} to phone {}", otp, phoneNumber);
    }

    @Override
    public void sendTicketSms(String phoneNumber, String pnr, String link) {
        log.info("[MOCK SMS] Sending Ticket link for PNR {} to phone {}", pnr, phoneNumber);
    }
}
