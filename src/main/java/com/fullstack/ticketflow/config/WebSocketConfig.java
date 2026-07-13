package com.fullstack.ticketflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker simple en memoria para destinos /topic/*.
        // Heartbeats cada 10s en ambos sentidos: mantienen viva la conexión y
        // detectan clientes caídos, evitando que el transporte quede colgado y
        // retrase los mensajes de stock. Requiere un TaskScheduler dedicado.
        config.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(webSocketHeartbeatScheduler());

        // Prefijo para los mensajes que los clientes envían hacia el backend.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint central de conexión para el Frontend (ws://host:8080/ws).
        // Al estar en 2 servidores distintos, allowedOriginPatterns("*") evita
        // errores de CORS. SockJS provee fallback para navegadores antiguos.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Bean
    public TaskScheduler webSocketHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }
}
