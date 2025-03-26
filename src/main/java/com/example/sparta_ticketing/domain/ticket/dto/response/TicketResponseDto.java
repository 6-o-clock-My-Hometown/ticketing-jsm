package com.example.sparta_ticketing.domain.ticket.dto.response;

import lombok.Getter;

@Getter
public class TicketResponseDto {

    private final Long id;
    private final String showTitle;
    private final String username;
    private final String seatType;

    public TicketResponseDto(Long id, String showTitle, String username, String seatType) {
        this.id = id;
        this.showTitle = showTitle;
        this.username = username;
        this.seatType = seatType;
    }
}
