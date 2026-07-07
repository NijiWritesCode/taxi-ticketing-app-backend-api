package com.busgo.backend.service;

import com.busgo.backend.dto.BookingRequest;
import com.busgo.backend.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);
    List<BookingResponse> getUserBookings(String userEmail);
    void cancelBooking(Long bookingId, String userEmail);
}
