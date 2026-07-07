package com.busgo.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/tracking")
public class TrackingController {

    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getTracking(@PathVariable String tripId) {
        return ResponseEntity.ok(Map.of(
            "tripId", tripId,
            "status", "in_transit",
            "bus", Map.of("currentLocation", Map.of("latitude", 7.6, "longitude", 4.5), "speed", 85, "heading", 45),
            "driver", Map.of("name", "Oluwaseun Adebayo", "phone", "+2348000000000", "avatarUrl", "https://images.unsplash.com/photo-1506277886164-e25aa3f4ef7f?w=100"),
            "vehicle", Map.of("type", "Toyota Hiace", "plateNumber", "KJA-123-XY"),
            "eta", Map.of("minutes", 42, "distanceKm", 58.3, "arrivalTime", "05:00 PM"),
            "route", List.of(
                Map.of("latitude", 6.5244, "longitude", 3.3792),
                Map.of("latitude", 6.9, "longitude", 3.8),
                Map.of("latitude", 7.6, "longitude", 4.5),
                Map.of("latitude", 8.5, "longitude", 6.2),
                Map.of("latitude", 9.0765, "longitude", 7.3986)
            )
        ));
    }
}
