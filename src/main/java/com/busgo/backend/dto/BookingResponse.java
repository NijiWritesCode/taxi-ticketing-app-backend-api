package com.busgo.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Long bookingId;
    private String pnr;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime expiresAt;
}
