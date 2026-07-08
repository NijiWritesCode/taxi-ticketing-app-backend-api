package com.busgo.backend.controller;

import com.busgo.backend.model.*;
import com.busgo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/buses")
@RequiredArgsConstructor
public class BusSearchController {

    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final SeatLockRepository seatLockRepository;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBuses(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date,
            @RequestParam int passengers) {

        LocalDate parsedDate = LocalDate.parse(date);
        LocalDateTime startOfDay = parsedDate.atStartOfDay();
        LocalDateTime endOfDay = parsedDate.atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findSchedulesByRouteAndDate(origin, destination, startOfDay, endOfDay);

        List<Map<String, Object>> results = schedules.stream().map(schedule -> {
            Bus bus = schedule.getBus();
            Operator operator = bus.getOperator();

            List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByScheduleId(schedule.getId());
            List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsByScheduleId(schedule.getId(), LocalDateTime.now());
            int takenSeats = bookedSeatIds.size() + lockedSeatIds.size();
            int availableSeats = Math.max(0, bus.getTotalSeats() - takenSeats);

            Map<String, Object> operatorMap = new HashMap<>();
            operatorMap.put("id", operator.getId());
            operatorMap.put("name", operator.getName());
            operatorMap.put("rating", operator.getRating());
            operatorMap.put("totalReviews", 1200);

            Map<String, Object> busMap = new HashMap<>();
            busMap.put("id", schedule.getId()); // Frontend treats scheduleId as the unique result ID
            busMap.put("operator", operatorMap);
            busMap.put("departureTime", schedule.getDepartureTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            busMap.put("arrivalTime", schedule.getArrivalTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            busMap.put("duration", (schedule.getRoute().getEstimatedDurationMins() / 60) + "h " + (schedule.getRoute().getEstimatedDurationMins() % 60) + "m");
            busMap.put("price", schedule.getBasePrice());
            busMap.put("currency", "₦");
            busMap.put("busType", bus.getBusType());
            busMap.put("totalSeats", bus.getTotalSeats());
            busMap.put("availableSeats", availableSeats);
            busMap.put("amenities", Map.of("ac", true, "usb", true));
            busMap.put("isFastest", false);
            busMap.put("isCheapest", false);
            return busMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "origin", origin,
            "destination", destination,
            "date", date,
            "results", results
        ));
    }

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<Map<String, Object>> getSeats(@PathVariable Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
        Bus bus = schedule.getBus();

        List<Seat> allSeats = seatRepository.findByBusId(bus.getId());
        List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByScheduleId(schedule.getId());
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsByScheduleId(schedule.getId(), LocalDateTime.now());

        List<Map<String, Object>> seats = allSeats.stream().map(seat -> {
            boolean isBooked = bookedSeatIds.contains(seat.getId()) || lockedSeatIds.contains(seat.getId());
            
            String[] parts = seat.getSeatNumber().split("_");
            int row = parts.length == 2 ? Integer.parseInt(parts[0]) : 0;
            int col = parts.length == 2 ? Integer.parseInt(parts[1]) : 0;
            String label = row == 0 ? "Driver" : (row + (col == 0 ? "A" : col == 1 ? "B" : col == 2 ? "C" : "D"));

            Map<String, Object> seatMap = new HashMap<>();
            seatMap.put("id", seat.getId()); // Frontend must send this as the seatId!
            seatMap.put("row", row);
            seatMap.put("column", col);
            seatMap.put("status", isBooked ? "booked" : "available");
            seatMap.put("type", seat.getSeatType());
            seatMap.put("price", schedule.getBasePrice());
            seatMap.put("label", label);
            return seatMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "scheduleId", scheduleId,
            "layout", Map.of(
                "id", "layout_1",
                "name", bus.getBusType() + " " + bus.getTotalSeats() + " Seater",
                "rows", 11,
                "columns", 4,
                "seats", seats
            )
        ));
    }
}
