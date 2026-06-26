package com.fullstack.ticketflow.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findAllByOrganizer_Email(String email, Pageable pageable);

    // Filtro dinámico cruzado: Busca por título, por la ciudad del Venue, por
    // categoría del evento y por rango de precios de sus TicketTypes
    @Query("SELECT DISTINCT e FROM Event e " +
            "JOIN e.venue v " +
            "LEFT JOIN e.ticketTypes t " +
            "WHERE (:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:city IS NULL OR LOWER(v.city) = LOWER(:city)) " +
            "AND (:categoryId IS NULL OR e.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR t.price <= :maxPrice) " +
            "AND e.status = 'ACTIVE'")
    Page<Event> findFilteredEvents(
            @Param("title") String title,
            @Param("city") String city,
            @Param("categoryId") Short categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
