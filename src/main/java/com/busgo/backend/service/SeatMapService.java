package com.busgo.backend.service;

import com.busgo.backend.dto.SeatLayoutResponse;

public interface SeatMapService {
    SeatLayoutResponse getSeatLayout(Long scheduleId);
}
