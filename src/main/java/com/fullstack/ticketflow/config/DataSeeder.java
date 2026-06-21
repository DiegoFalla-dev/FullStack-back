package com.fullstack.ticketflow.config;

import com.fullstack.ticketflow.role.Role;
import com.fullstack.ticketflow.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole("ADMIN");
        seedRole("ORGANIZER");
        seedRole("CLIENT");
    }

    private void seedRole(String name) {
        if (!roleRepository.existsByName(name)) {
            roleRepository.save(Role.builder().name(name).build());
        }
    }
}
