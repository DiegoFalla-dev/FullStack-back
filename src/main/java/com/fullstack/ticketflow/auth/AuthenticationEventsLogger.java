package com.fullstack.ticketflow.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

// Registra de forma centralizada TODOS los éxitos y fracasos de
// autenticación que publica Spring Security, para que la validación del
// login sea visible en los logs.
@Slf4j
@Component
public class AuthenticationEventsLogger {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("SECURITY | autenticación correcta: {}", event.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("SECURITY | autenticación fallida: {} -> {}",
                event.getAuthentication().getName(),
                event.getException().getMessage());
    }
}
