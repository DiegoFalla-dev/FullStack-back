package com.fullstack.ticketflow.event;

import com.fullstack.ticketflow.event.dto.EventRequest;
import com.fullstack.ticketflow.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface EventService {
    Page<EventResponse> search(String title, String city, Short categoryId, BigDecimal minPrice, BigDecimal maxPrice, boolean upcomingOnly, Pageable pageable);
    Page<EventResponse> listMine(String organizerEmail, Pageable pageable);
    EventResponse getById(Integer id);
    EventResponse create(String organizerEmail, EventRequest request);
    EventResponse update(Integer id, String organizerEmail, EventRequest request);
    void delete(Integer id, String userEmail);
}
