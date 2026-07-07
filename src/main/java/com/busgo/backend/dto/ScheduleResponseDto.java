package com.busgo.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ScheduleResponseDto {
    private Long scheduleId;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private String busOperatorName;
    private String busType;
    private Integer totalSeats;
    private String status;
}
