package com.busgo.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    private Long scheduleId;
    private List<PassengerDto> passengers;
}
