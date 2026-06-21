package com.fullstack.ticketflow.auth.dto;

public record LoginResponse(String token, String userId, String role) {
}
