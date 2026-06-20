package com.fullstack.ticketflow.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserIdOrderByPaidAtDesc(String userId);
    List<Order> findAllByOrderByPaidAtDesc();

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderItems oi
            JOIN oi.ticketType tt
            JOIN tt.event e
            JOIN e.organizer org
            WHERE org.email = :organizerEmail
            ORDER BY o.paidAt DESC
            """)
    List<Order> findSalesByOrganizerEmail(@Param("organizerEmail") String organizerEmail);
}
