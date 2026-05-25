package com.fullstack.ticketflow.auth.dto;

public record RegisterResponse(
        String userId,
        String email,
        String role
) {
}
