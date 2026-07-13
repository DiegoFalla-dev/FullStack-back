package com.fullstack.ticketflow.event;

import com.fullstack.ticketflow.category.Category;
import com.fullstack.ticketflow.category.CategoryRepository;
import com.fullstack.ticketflow.event.dto.EventRequest;
import com.fullstack.ticketflow.event.dto.EventResponse;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.tickettype.TicketType;
import com.fullstack.ticketflow.user.User;
import com.fullstack.ticketflow.user.UserRepository;
import com.fullstack.ticketflow.venue.Venue;
import com.fullstack.ticketflow.venue.VenueRepository;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<EventResponse> search(String title, String city, Short categoryId, BigDecimal minPrice, BigDecimal maxPrice, boolean upcomingOnly, Pageable pageable) {
        return eventRepository.findFilteredEvents(title, city, categoryId, minPrice, maxPrice, upcomingOnly, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventResponse> listMine(String organizerEmail, Pageable pageable) {
        return eventRepository.findAllByOrganizer_Email(organizerEmail, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public EventResponse getById(Integer id) {
        return eventRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
    }

    @Transactional
    @Override
    public EventResponse create(String organizerEmail, EventRequest request) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado"));
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .dateTime(request.dateTime())
                .imageUrl(request.imageUrl())
                .status("ACTIVE")
                .venue(venue)
                .organizer(organizer)
                .category(category)
                .build();

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventResponse update(Integer id, String organizerEmail, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        User user = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!isAdmin(user) && !event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new BusinessRuleException("No tienes permisos para modificar este evento.");
        }

        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setDateTime(request.dateTime());
        event.setImageUrl(request.imageUrl());
        event.setVenue(venue);
        event.setCategory(category);

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional
    @Override
    public void delete(Integer id, String userEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!isAdmin(user) && !event.getOrganizer().getEmail().equals(userEmail)) {
            throw new BusinessRuleException("No tienes permisos para eliminar este evento.");
        }

        boolean hasSales = event.getTicketTypes().stream().anyMatch(tt -> tt.getSoldQty() > 0);

        if (hasSales && !isAdmin(user)) {
            throw new BusinessRuleException("No se puede eliminar el evento porque ya existen entradas vendidas. Contacta al administrador.");
        }

        if (hasSales) {
            event.setStatus("CANCELLED");
            eventRepository.save(event);
            return;
        }

        eventRepository.delete(event);
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
    }

    private EventResponse mapToResponse(Event event) {
        BigDecimal minPrice = event.getTicketTypes().stream()
                .map(TicketType::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        VenueResponse venueResp = new VenueResponse(
                event.getVenue().getId(),
                event.getVenue().getName(),
                event.getVenue().getAddress(),
                event.getVenue().getCity(),
                event.getVenue().getCapacity(),
                event.getVenue().getCreatedAt(),
                event.getVenue().getUpdatedAt()
        );

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime(),
                event.getImageUrl(),
                event.getStatus(),
                venueResp,
                event.getOrganizer().getFullName(),
                minPrice,
                event.getCategory() != null ? event.getCategory().getId() : null,
                event.getCategory() != null ? event.getCategory().getName() : null
        );
    }
}
