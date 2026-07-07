package com.busgo.backend.service;
import com.busgo.backend.model.Booking;
import com.busgo.backend.model.Payment;
import com.busgo.backend.repository.BookingRepository;
import com.busgo.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final TicketService ticketService;

    @Transactional
    public String handleSuccessfulPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        if (payment.getStatus().equals("COMPLETED")) return "Already processed";

        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        ticketService.generateTicket(booking);
        
        return "Webhook processed successfully! Ticket generated.";
    }
}
