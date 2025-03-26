package com.example.sparta_ticketing.domain.ticket.controller;

import com.example.sparta_ticketing.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.sparta_ticketing.domain.ticket.dto.response.TicketResponseDto;
import com.example.sparta_ticketing.domain.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    public ResponseEntity<Long> createTicket(@Valid @RequestBody CreateTicketRequestDto dto) {
        return ResponseEntity.ok(ticketService.issueTicket(dto));
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketResponseDto> getTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicket(ticketId));
    }

    @PatchMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> cancelTicketing(@PathVariable Long ticketId) {
        ticketService.cancelTicketing(ticketId);
        return ResponseEntity.ok().build();
    }
}
