package com.fullstack.ticketflow.user.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 120) String fullName
) {
}
