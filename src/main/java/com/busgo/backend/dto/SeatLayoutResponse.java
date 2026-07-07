package com.busgo.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SeatLayoutResponse {
    private Long scheduleId;
    private Long busId;
    private String busType;
    private List<SeatStatusDto> seats;
}
