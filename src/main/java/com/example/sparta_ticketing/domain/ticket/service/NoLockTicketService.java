package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.sparta_ticketing.domain.ticket.dto.response.TicketResponseDto;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.ticket.repository.TicketRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoLockTicketService {

    private final TicketRepository ticketRepository;
    private final ShowService showService;
    private final UserService userService;
    private final SeatService seatService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SeatRepository seatRepository;



    @Transactional
    public Long issueTicket(@Valid CreateTicketRequestDto dto) {

        User user = userService.findUser(dto.getUserId());
        Show show = showService.getShow(dto.getShowId());
        Seat seat = seatService.findSeat(dto.getSeatId());



        seat.setSeatCount(seat.getCount() - 1);
//        seatRepository.save(seat);
        seatRepository.flush();

        if (seat.getCount() < 0) {
            throw new IllegalStateException("좌석이 매진되었습니다.");
        }

        Ticket ticket = new Ticket(user, show, seat);

        Ticket savedTicket = ticketRepository.save(ticket);

        return savedTicket.getId();
    }

    @Transactional(readOnly = true)
    public TicketResponseDto getTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("해당 티켓이 존재하지 않습니다"));

        // 취소된 티켓인지 확인
        if (ticket.isCanceled()) {
            throw new InvalidRequestException("해당 티켓은 취소되었습니다");
        }
        return new TicketResponseDto(ticket.getId(),
                ticket.getShow().getTitle(),
                ticket.getUser().getNickname(),
                String.valueOf(ticket.getSeat().getName()));
    }

    @Transactional
    public void cancelTicketing(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new InvalidRequestException("해당 티켓이 존재하지 않습니다"));
        ticket.getSeat().restoreSeatCount();
        // 소프트딜리트
        ticket.cancelTicketing();
    }
}
