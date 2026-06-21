package com.fullstack.ticketflow.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 120) String fullName,
        @NotNull Short roleId
) {
}
