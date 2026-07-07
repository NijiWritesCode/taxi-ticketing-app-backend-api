package com.busgo.backend.service;
import com.busgo.backend.dto.PaymentInitiateResponse;
import com.busgo.backend.model.Booking;
import com.busgo.backend.model.Payment;
import com.busgo.backend.repository.BookingRepository;
import com.busgo.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentInitiateResponse initiatePayment(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (!booking.getUser().getEmail().equals(userEmail)) throw new RuntimeException("Unauthorized");
        if (!booking.getStatus().equals("PENDING")) throw new RuntimeException("Booking is not pending");

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod("PAYSTACK");
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        payment.setStatus("PENDING");
        payment = paymentRepository.save(payment);

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .checkoutUrl("https://mock-paystack.com/checkout/" + payment.getTransactionId())
                .status("PENDING")
                .build();
    }
}
