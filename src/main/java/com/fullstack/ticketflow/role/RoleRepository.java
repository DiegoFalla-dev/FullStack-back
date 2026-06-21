package com.fullstack.ticketflow.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    Page<Role> findAll(Pageable pageable);
}
