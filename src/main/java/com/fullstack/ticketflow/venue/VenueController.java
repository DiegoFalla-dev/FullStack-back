package com.fullstack.ticketflow.venue;

import com.fullstack.ticketflow.shared.response.ApiResponse;
import com.fullstack.ticketflow.venue.dto.VenueRequest;
import com.fullstack.ticketflow.venue.dto.VenueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    // GET es público o accesible por clientes/organizadores
    @GetMapping
    public ResponseEntity<ApiResponse<Page<VenueResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(venueService.list(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(venueService.getById(id)));
    }

    // POST, PUT y DELETE bloqueados solo para el Administrador
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<VenueResponse>> create(@Valid @RequestBody VenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(venueService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueResponse>> update(@PathVariable Integer id, @Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(venueService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        venueService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}