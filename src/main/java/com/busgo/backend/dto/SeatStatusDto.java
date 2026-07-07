package com.busgo.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatStatusDto {
    private Long seatId;
    private String seatNumber;
    private String seatType;
    private Boolean isLadiesOnly;
    private String status; // AVAILABLE, BOOKED, LOCKED
}
