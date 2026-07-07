package com.busgo.backend.controller;
import com.busgo.backend.service.ProceduralEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/v1/buses")
@RequiredArgsConstructor
public class BusSearchController {

    private final ProceduralEngine proceduralEngine;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBuses(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date,
            @RequestParam int passengers) {
        
        Random rand = proceduralEngine.getDeterministicRandom(origin, destination, date);
        int numBuses = rand.nextInt(6) + 3; // 3 to 8 buses

        List<Map<String, Object>> results = new ArrayList<>();
        int basePrice = (rand.nextInt(15) + 10) * 1000; // 10k to 25k

        for (int i = 0; i < numBuses; i++) {
            Map<String, Object> bus = new HashMap<>();
            String busId = "bus_" + Math.abs(rand.nextInt(900000) + 100000);
            bus.put("id", busId);
            Map<String, Object> operator = new HashMap<>();
            operator.put("id", "op_" + i);
            operator.put("name", (i % 2 == 0) ? "GIGM" : "God is Good Motors");
            operator.put("logoUrl", null);
            operator.put("rating", 4.0 + (rand.nextDouble() % 1.0));
            operator.put("totalReviews", rand.nextInt(2000) + 100);
            bus.put("operator", operator);
            
            int departureHour = rand.nextInt(14) + 6; // 6 AM to 8 PM
            bus.put("departureTime", String.format("%02d:00 AM", departureHour)); // Mock format
            bus.put("arrivalTime", String.format("%02d:00 PM", (departureHour + 10) % 12));
            bus.put("duration", "10h 30m");
            bus.put("price", basePrice + (rand.nextInt(5) * 1000));
            bus.put("currency", "₦");
            bus.put("busType", "AC Jet");
            bus.put("totalSeats", 41);
            bus.put("availableSeats", rand.nextInt(20) + 5);
            bus.put("amenities", Map.of("ac", true, "usb", true, "wifi", false));
            bus.put("isFastest", i == 0);
            bus.put("isCheapest", i == 1);
            
            results.add(bus);
        }

        return ResponseEntity.ok(Map.of(
            "origin", origin,
            "destination", destination,
            "date", date,
            "results", results
        ));
    }

    @GetMapping("/{busId}/seats")
    public ResponseEntity<Map<String, Object>> getSeats(@PathVariable String busId) {
        Random rand = proceduralEngine.getDeterministicRandom(busId);
        
        List<Map<String, Object>> seats = new ArrayList<>();
        seats.add(Map.of("id", "seat_drv", "row", 0, "column", 0, "status", "available", "type", "driver", "price", 0, "label", "Driver"));
        
        for (int row = 1; row <= 10; row++) {
            for (int col = 0; col < 4; col++) {
                boolean isBooked = rand.nextBoolean();
                seats.add(Map.of(
                    "id", "seat_" + row + "_" + col,
                    "row", row,
                    "column", col,
                    "status", isBooked ? "booked" : "available",
                    "type", "standard",
                    "price", 25000,
                    "label", row + (col == 0 ? "A" : col == 1 ? "B" : col == 2 ? "C" : "D")
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
            "busId", busId,
            "layout", Map.of(
                "id", "layout_1",
                "name", "Standard 41 Seater",
                "rows", 11,
                "columns", 4,
                "seats", seats
            )
        ));
    }
}
