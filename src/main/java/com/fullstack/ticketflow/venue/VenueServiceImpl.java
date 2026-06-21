package com.fullstack.ticketflow.venue;

import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.venue.dto.VenueRequest;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository repository;
    private final VenueMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<VenueResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public VenueResponse getById(Integer id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));
    }

    @Transactional
    @Override
    public VenueResponse create(VenueRequest request) {
        Venue entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @Override
    public VenueResponse update(Integer id, VenueRequest request) {
        Venue venue = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));
        mapper.updateFromDto(request, venue);
        return mapper.toResponse(repository.save(venue));
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Lugar no encontrado");
        }
        repository.deleteById(id);
    }
}