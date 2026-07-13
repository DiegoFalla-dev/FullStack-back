package com.fullstack.ticketflow.tickettype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketType t WHERE t.id = :id")
    Optional<TicketType> findByIdWithLock(@Param("id") Integer id);

    // NUEVO: necesario para que el front liste las categorías de precio
    // de un evento (selector de compra, panel del organizador).
    List<TicketType> findAllByEventId(Integer eventId);

    @Query("SELECT COALESCE(SUM(t.totalQty), 0) FROM TicketType t " +
            "WHERE t.event.id = :eventId " +
            "AND (:excludeId IS NULL OR t.id <> :excludeId)")
    Long sumTotalQtyByEventIdExcludingId(
            @Param("eventId") Integer eventId,
            @Param("excludeId") Integer excludeId
    );
}
