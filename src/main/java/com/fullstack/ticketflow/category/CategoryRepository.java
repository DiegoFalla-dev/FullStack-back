package com.fullstack.ticketflow.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Short> {
    boolean existsByName(String name);
}
