package com.fullstack.ticketflow.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findAllByActiveTrue(Pageable pageable);
    Optional<User> findByIdAndActiveTrue(String id);
}
