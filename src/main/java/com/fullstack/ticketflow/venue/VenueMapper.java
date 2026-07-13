package com.fullstack.ticketflow.venue;

import com.fullstack.ticketflow.venue.dto.VenueRequest;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import org.springframework.stereotype.Component;

// Mapper manual (sin MapStruct) para no depender del procesamiento de
// anotaciones en el IDE.
@Component
public class VenueMapper {

    public Venue toEntity(VenueRequest request) {
        return Venue.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .capacity(request.capacity())
                .build();
    }

    public VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getCapacity(),
                venue.getCreatedAt(),
                venue.getUpdatedAt()
        );
    }

    public void updateFromDto(VenueRequest request, Venue entity) {
        entity.setName(request.name());
        entity.setAddress(request.address());
        entity.setCity(request.city());
        entity.setCapacity(request.capacity());
    }
}
