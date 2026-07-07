package com.busgo.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/trips")
public class TripController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTrips(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(Map.of(
            "trips", List.of(
                Map.of(
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
            )
        ));
    }
}
