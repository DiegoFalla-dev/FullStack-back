package com.fullstack.ticketflow.user;

import com.fullstack.ticketflow.user.dto.PasswordChangeRequest;
import com.fullstack.ticketflow.user.dto.UserRequest;
import com.fullstack.ticketflow.user.dto.UserResponse;
import com.fullstack.ticketflow.user.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> list(Pageable pageable);
    UserResponse getById(String id);
    UserResponse create(UserRequest request);
    UserResponse update(String id, UserUpdateRequest request);
    void changePassword(String id, PasswordChangeRequest request);
    void deactivate(String id);
}
