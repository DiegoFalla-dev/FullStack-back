package com.fullstack.ticketflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para enviar mensajes (Broadcasting) a los clientes
        // Los clientes se suscribirán a canales que empiecen con /topic
        config.enableSimpleBroker("/topic");

        // Prefijo para los mensajes que los clientes envían hacia el backend
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint central de conexión para el Frontend (ej. ws://localhost:8080/ws)
        // Al estar en 2 servidores distintos, allowedOriginPatterns("*") evita errores de CORS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Soporte de fallback si el navegador es antiguo
    }
}