package com.busgo.backend.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/v1/bookings")
public class BookingControllerV1 {

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> request) {
        return new ResponseEntity<>(Map.of(
            "booking", Map.of(
                "id", "trip_x9f2k",
                "pnr", "BRZ-8F2X9A",
                "status", "upcoming",
                "origin", Map.of("city", "Lagos", "code", "LOS", "time", "06:30 AM", "date", "15 Jul 2026"),
                "destination", Map.of("city", "Abuja", "code", "ABV", "time", "05:00 PM", "date", "15 Jul 2026"),
                "duration", "10h 30m",
                "ticketDetails", Map.of("terminal", "T1", "gate", "A", "seatNumber", "3A", "class", "Economy"),
                "passenger", Map.of("id", "usr_abc123", "fullName", "Adebayo Johnson", "email", "adebayo@email.com", "phone", "+2348012345678"),
                "farePaid", 26250,
                "colorScheme", "red"
            )
        ), HttpStatus.CREATED);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(Map.of(
            "id", bookingId,
            "status", "cancelled",
            "refundAmount", 26250,
            "refundStatus", "processing"
        ));
    }
}
