package com.fullstack.ticketflow.tickettype;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {
}
