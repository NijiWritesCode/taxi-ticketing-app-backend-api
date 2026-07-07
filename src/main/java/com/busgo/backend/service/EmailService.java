package com.busgo.backend.service;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, String pnr, String ticketDetails);
    void sendPaymentFailure(String toEmail, String pnr);
    void sendTripReminder(String toEmail, String pnr, String departureTime);
}
