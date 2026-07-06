package com.fullstack.ticketflow.tickettype;

import com.fullstack.ticketflow.event.Event;
import com.fullstack.ticketflow.event.EventRepository;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.tickettype.dto.TicketTypeRequest;
import com.fullstack.ticketflow.tickettype.dto.TicketTypeResponse;
import com.fullstack.ticketflow.user.User;
import com.fullstack.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TicketTypeResponse> listByEvent(Integer eventId) {
        return ticketTypeRepository.findAllByEventId(eventId).stream()
                .map(TicketTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketTypeResponse create(String requesterEmail, TicketTypeRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        assertCanModify(event, requesterEmail);

        TicketType ticketType = TicketType.builder()
                .event(event)
                .name(request.name())
                .price(request.price())
                .totalQty(request.totalQty())
                .soldQty(0)
                .build();

        return TicketTypeMapper.toResponse(ticketTypeRepository.save(ticketType));
    }

    @Override
    @Transactional
    public TicketTypeResponse update(Integer id, String requesterEmail, TicketTypeRequest request) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de entrada no encontrado"));

        assertCanModify(ticketType.getEvent(), requesterEmail);

        // Regla de negocio: totalQty no puede bajar por debajo de lo que
        // ya se vendió, rompería la integridad del aforo.
        if (request.totalQty() < ticketType.getSoldQty()) {
            throw new BusinessRuleException(
                    "totalQty no puede ser menor que las entradas ya vendidas (" + ticketType.getSoldQty() + ")");
        }

        ticketType.setName(request.name());
        ticketType.setPrice(request.price());
        ticketType.setTotalQty(request.totalQty());

        return TicketTypeMapper.toResponse(ticketTypeRepository.save(ticketType));
    }

    @Override
    @Transactional
    public void delete(Integer id, String requesterEmail) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de entrada no encontrado"));

        assertCanModify(ticketType.getEvent(), requesterEmail);

        // Regla de negocio: no se elimina una categoría con ventas ya
        // confirmadas — esos clientes ya tienen tickets emitidos.
        if (ticketType.getSoldQty() > 0) {
            throw new BusinessRuleException("No se puede eliminar un tipo de entrada con entradas vendidas");
        }

        ticketTypeRepository.delete(ticketType);
    }

    private void assertCanModify(Event event, String requesterEmail) {
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        boolean isOwner = event.getOrganizer().getEmail().equals(requesterEmail);

        if (!isOwner && !isAdmin) {
            throw new BusinessRuleException("Solo el organizador del evento puede modificar sus categorías de precio");
        }
    }
}