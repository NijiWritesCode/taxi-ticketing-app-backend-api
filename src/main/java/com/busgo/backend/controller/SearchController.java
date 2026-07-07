package com.busgo.backend.controller;

import com.busgo.backend.dto.ScheduleResponseDto;
import com.busgo.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponseDto>> searchSchedules(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate) {
        return ResponseEntity.ok(searchService.searchSchedules(origin, destination, travelDate));
    }
}
