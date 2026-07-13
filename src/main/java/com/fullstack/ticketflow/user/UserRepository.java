package com.fullstack.ticketflow.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByIdAndActiveTrue(String id);
}
