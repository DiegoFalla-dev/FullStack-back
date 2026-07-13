package com.fullstack.ticketflow.user;

import com.fullstack.ticketflow.user.dto.UserRequest;
import com.fullstack.ticketflow.user.dto.UserResponse;
import com.fullstack.ticketflow.user.dto.UserUpdateRequest;
import org.springframework.stereotype.Component;

// Mapper manual (sin MapStruct) para no depender del procesamiento de
// anotaciones en el IDE. El role, pwdHash y active los gestiona el
// servicio (no se mapean aquí).
@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole() != null ? user.getRole().getId() : null,
                user.getRole() != null ? user.getRole().getName() : null,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public void updateFromDto(UserUpdateRequest request, User entity) {
        entity.setFullName(request.fullName());
    }
}
