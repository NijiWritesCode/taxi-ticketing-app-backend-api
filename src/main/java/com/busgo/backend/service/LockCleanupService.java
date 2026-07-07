package com.busgo.backend.service;

import com.busgo.backend.model.Booking;
import com.busgo.backend.repository.BookingRepository;
import com.busgo.backend.repository.SeatLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockCleanupService {

    private final BookingRepository bookingRepository;
    private final SeatLockRepository seatLockRepository;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void cleanupExpiredBookingsAndLocks() {
        log.info("Running Lock Cleanup Task...");

        // 1. Delete all SeatLocks where expiresAt < now
        seatLockRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        // 2. Cancel any PENDING bookings that are older than 10 minutes
        List<Booking> pendingBookings = bookingRepository.findByStatus("PENDING");
        LocalDateTime tenMinsAgo = LocalDateTime.now().minusMinutes(10);
        
        for (Booking booking : pendingBookings) {
            if (booking.getCreatedAt().isBefore(tenMinsAgo)) {
                booking.setStatus("EXPIRED");
                bookingRepository.save(booking);
                log.info("Expired booking canceled: " + booking.getPnr());
            }
        }
    }
}
