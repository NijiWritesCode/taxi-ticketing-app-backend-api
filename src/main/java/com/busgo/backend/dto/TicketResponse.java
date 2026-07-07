package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class TicketResponse {
    private String pnr;
    private String ticketNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String qrCodeImage;
    private List<String> seatNumbers;
}
