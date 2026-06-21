package com.fullstack.ticketflow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @Size(max = 120) String fullName,
        @NotNull Short roleId
) {
}
