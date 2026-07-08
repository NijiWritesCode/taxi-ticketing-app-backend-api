package com.busgo.backend.service;
import com.busgo.backend.dto.TicketResponse;
import com.busgo.backend.model.Booking;
import com.busgo.backend.model.BookedSeat;
import com.busgo.backend.model.Ticket;
import com.busgo.backend.repository.BookedSeatRepository;
import com.busgo.backend.repository.BookingRepository;
import com.busgo.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    @Transactional
    public void generateTicket(Booking booking) {
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setTicketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        String qrContent = "PNR:" + booking.getPnr() + "|TKT:" + ticket.getTicketNumber();
        ticket.setQrCodeUrl(qrCodeService.generateQrCodeBase64(qrContent));
        
        ticketRepository.save(ticket);

        // Dispatch E-Ticket Email with Attachment
        byte[] qrBytes = null;
        try {
            String base64Image = ticket.getQrCodeUrl().split(",")[1];
            qrBytes = java.util.Base64.getDecoder().decode(base64Image);
        } catch(Exception e) {}

        String details = "Origin: " + booking.getSchedule().getRoute().getOrigin() + 
                         " | Destination: " + booking.getSchedule().getRoute().getDestination() + 
                         " | Departure: " + booking.getSchedule().getDepartureTime();
                         
        emailService.sendTicketEmail(
            booking.getUser().getEmail(), 
            booking.getUser().getFullName(), 
            booking.getPnr(), 
            details, 
            qrBytes
        );
    }

    public TicketResponse getTicket(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized");
        }
        Ticket ticket = ticketRepository.findByBookingId(bookingId).orElseThrow(() -> new RuntimeException("Ticket not yet generated"));
        
        List<BookedSeat> bookedSeats = bookedSeatRepository.findByBookingId(bookingId);
                
        List<String> seats = bookedSeats.stream().map(bs -> bs.getSeat().getSeatNumber()).collect(Collectors.toList());

        return TicketResponse.builder()
                .pnr(booking.getPnr())
                .ticketNumber(ticket.getTicketNumber())
                .origin(booking.getSchedule().getRoute().getOrigin())
                .destination(booking.getSchedule().getRoute().getDestination())
                .departureTime(booking.getSchedule().getDepartureTime().toString())
                .qrCodeImage(ticket.getQrCodeUrl())
                .seatNumbers(seats)
                .build();
    }
}
