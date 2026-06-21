package com.fullstack.ticketflow.auth;

import com.fullstack.ticketflow.auth.dto.LoginRequest;
import com.fullstack.ticketflow.auth.dto.LoginResponse;
import com.fullstack.ticketflow.auth.dto.RegisterRequest;
import com.fullstack.ticketflow.auth.dto.RegisterResponse;
import com.fullstack.ticketflow.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth-controller", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password to get JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user with email, password, and role")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.register(request)));
    }
}
