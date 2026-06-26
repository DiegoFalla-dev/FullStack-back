package com.fullstack.ticketflow.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Short> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    Page<Category> findAll(Pageable pageable);
}
