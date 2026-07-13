package com.fullstack.ticketflow.shared.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.ticketflow.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Devuelve 403 con cuerpo JSON ApiResponse cuando un usuario autenticado
// no tiene permisos para el recurso (denegación a nivel de filtro de seguridad).
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), ApiResponse.error("No tienes permisos para esta acción."));
    }
}
