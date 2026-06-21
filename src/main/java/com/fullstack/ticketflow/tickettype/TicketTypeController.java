package com.fullstack.ticketflow.tickettype;

import com.fullstack.ticketflow.shared.response.ApiResponse;
import com.fullstack.ticketflow.tickettype.dto.TicketTypeRequest;
import com.fullstack.ticketflow.tickettype.dto.TicketTypeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    // Lectura pública: el detalle del evento muestra sus categorías de
    // precio sin requerir login.
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketTypeResponse>>> listByEvent(@RequestParam Integer eventId) {
        return ResponseEntity.ok(ApiResponse.success(ticketTypeService.listByEvent(eventId)));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TicketTypeResponse>> create(
            @Valid @RequestBody TicketTypeRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ticketTypeService.create(authentication.getName(), request)));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketTypeResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody TicketTypeRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(ticketTypeService.update(id, authentication.getName(), request)));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id, Authentication authentication) {
        ticketTypeService.delete(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}