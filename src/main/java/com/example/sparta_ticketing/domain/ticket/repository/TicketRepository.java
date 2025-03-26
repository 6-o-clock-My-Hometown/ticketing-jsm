package com.example.sparta_ticketing.domain.ticket.repository;

import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
