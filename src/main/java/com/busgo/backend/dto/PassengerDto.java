package com.busgo.backend.dto;

import lombok.Data;

@Data
public class PassengerDto {
    private Long seatId;
    private String passengerName;
    private Integer passengerAge;
    private String passengerGender;
}
