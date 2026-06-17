package com.fullstack.ticketflow.venue;

import com.fullstack.ticketflow.venue.dto.VenueRequest;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VenueService {
    Page<VenueResponse> list(Pageable pageable);
    VenueResponse getById(Integer id);
    VenueResponse create(VenueRequest request);
    VenueResponse update(Integer id, VenueRequest request);
    void delete(Integer id);
}