package com.fullstack.ticketflow.config;

import com.fullstack.ticketflow.category.Category;
import com.fullstack.ticketflow.category.CategoryRepository;
import com.fullstack.ticketflow.role.Role;
import com.fullstack.ticketflow.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        // Resiliencia: cada elemento se siembra de forma independiente, de modo
        // que un fallo en uno NO impida crear los demás ni detenga el arranque.
        seedRole("ADMIN");
        seedRole("ORGANIZER");
        seedRole("CLIENT");

        seedCategory("Concierto");
        seedCategory("Teatro");
        seedCategory("Deportes");
        seedCategory("Stand-up");
        seedCategory("Festival");
    }

    private void seedRole(String name) {
        try {
            if (!roleRepository.existsByName(name)) {
                roleRepository.save(Role.builder().name(name).build());
                log.info("DataSeeder: rol '{}' creado", name);
            }
        } catch (Exception ex) {
            log.error("DataSeeder: no se pudo sembrar el rol '{}': {}", name, ex.getMessage());
        }
    }

    private void seedCategory(String name) {
        try {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(Category.builder().name(name).build());
                log.info("DataSeeder: categoría '{}' creada", name);
            }
        } catch (Exception ex) {
            log.error("DataSeeder: no se pudo sembrar la categoría '{}': {}", name, ex.getMessage());
        }
    }
}
