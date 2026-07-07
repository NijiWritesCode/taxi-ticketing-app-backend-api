package com.busgo.backend.service.impl;

import com.busgo.backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!prod") // Active when not in prod profile
public class MockEmailService implements EmailService {

    @Override
    public void sendBookingConfirmation(String toEmail, String pnr, String ticketDetails) {
        log.info("[MOCK EMAIL] Sending Booking Confirmation to {} for PNR: {}", toEmail, pnr);
    }

    @Override
    public void sendPaymentFailure(String toEmail, String pnr) {
        log.info("[MOCK EMAIL] Sending Payment Failure to {} for PNR: {}", toEmail, pnr);
    }

    @Override
    public void sendTripReminder(String toEmail, String pnr, String departureTime) {
        log.info("[MOCK EMAIL] Sending Trip Reminder to {} for PNR: {}", toEmail, pnr);
    }
}
