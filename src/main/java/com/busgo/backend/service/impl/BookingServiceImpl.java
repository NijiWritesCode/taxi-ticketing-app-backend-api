package com.busgo.backend.service.impl;

import com.busgo.backend.dto.BookingRequest;
import com.busgo.backend.dto.BookingResponse;
import com.busgo.backend.dto.PassengerDto;
import com.busgo.backend.exception.ResourceNotFoundException;
import com.busgo.backend.model.*;
import com.busgo.backend.repository.*;
import com.busgo.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final SeatLockRepository seatLockRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // 1. Check availability
        List<Long> requestedSeatIds = request.getPassengers().stream()
                .map(PassengerDto::getSeatId)
                .collect(Collectors.toList());

        List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByScheduleId(schedule.getId());
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsByScheduleId(schedule.getId(), LocalDateTime.now());

        for (Long seatId : requestedSeatIds) {
            if (bookedSeatIds.contains(seatId) || lockedSeatIds.contains(seatId)) {
                throw new RuntimeException("One or more selected seats are no longer available.");
            }
        }

        // 2. Create Locks
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        for (Long seatId : requestedSeatIds) {
            Seat seat = seatRepository.findById(seatId).orElseThrow();
            SeatLock lock = new SeatLock();
            lock.setSchedule(schedule);
            lock.setSeat(seat);
            lock.setLockedBy(user);
            lock.setExpiresAt(expiresAt);
            seatLockRepository.save(lock);
        }

        // 3. Create Booking
        BigDecimal totalAmount = schedule.getBasePrice().multiply(BigDecimal.valueOf(requestedSeatIds.size()));
        String pnr = "BUSGO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking();
        booking.setPnr(pnr);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("PENDING");
        booking = bookingRepository.save(booking);

        // 4. Create BookedSeats
        for (PassengerDto dto : request.getPassengers()) {
            Seat seat = seatRepository.findById(dto.getSeatId()).orElseThrow();
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setBooking(booking);
            bookedSeat.setSeat(seat);
            bookedSeat.setPassengerName(dto.getPassengerName());
            bookedSeat.setPassengerAge(dto.getPassengerAge());
            bookedSeat.setPassengerGender(dto.getPassengerGender());
            bookedSeatRepository.save(bookedSeat);
        }

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .pnr(booking.getPnr())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserId(user.getId()).stream()
                .map(b -> BookingResponse.builder()
                        .bookingId(b.getId())
                        .pnr(b.getPnr())
                        .totalAmount(b.getTotalAmount())
                        .status(b.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
