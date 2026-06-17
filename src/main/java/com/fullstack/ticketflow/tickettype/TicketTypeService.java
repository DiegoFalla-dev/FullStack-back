package com.fullstack.ticketflow.tickettype;

import com.fullstack.ticketflow.tickettype.dto.TicketTypeRequest;
import com.fullstack.ticketflow.tickettype.dto.TicketTypeResponse;

import java.util.List;

public interface TicketTypeService {
    List<TicketTypeResponse> listByEvent(Integer eventId);
    TicketTypeResponse create(String requesterEmail, TicketTypeRequest request);
    TicketTypeResponse update(Integer id, String requesterEmail, TicketTypeRequest request);
    void delete(Integer id, String requesterEmail);
}