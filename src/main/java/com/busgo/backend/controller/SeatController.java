package com.busgo.backend.controller;

import com.busgo.backend.dto.SeatLayoutResponse;
import com.busgo.backend.service.SeatMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class SeatController {

    private final SeatMapService seatMapService;

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<SeatLayoutResponse> getSeatLayout(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(seatMapService.getSeatLayout(scheduleId));
    }
}
