package com.busgo.backend.controller;
import com.busgo.backend.dto.TicketResponse;
import com.busgo.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long bookingId, Authentication authentication) {
        return ResponseEntity.ok(ticketService.getTicket(bookingId, authentication.getName()));
    }
}
