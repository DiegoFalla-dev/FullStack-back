package com.fullstack.ticketflow.venue;

import com.fullstack.ticketflow.venue.dto.VenueRequest;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Venue toEntity(VenueRequest request);

    VenueResponse toResponse(Venue venue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(VenueRequest request, @MappingTarget Venue entity);
}