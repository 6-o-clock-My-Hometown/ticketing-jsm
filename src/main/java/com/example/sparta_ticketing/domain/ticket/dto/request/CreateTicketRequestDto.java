package com.example.sparta_ticketing.domain.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTicketRequestDto {

    @NotBlank(message = "유저 ID는 필수입니다")
    private Long userId;

    @NotBlank(message = "좌석 ID는 필수입니다")
    private Long seatId;

    @NotBlank(message = "공연 ID는 필수입니다")
    private Long showId;

}
