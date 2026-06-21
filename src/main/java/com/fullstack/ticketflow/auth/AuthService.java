package com.fullstack.ticketflow.auth;

import com.fullstack.ticketflow.auth.dto.LoginRequest;
import com.fullstack.ticketflow.auth.dto.LoginResponse;
import com.fullstack.ticketflow.auth.dto.RegisterRequest;
import com.fullstack.ticketflow.auth.dto.RegisterResponse;
import com.fullstack.ticketflow.config.JwtService;
import com.fullstack.ticketflow.role.Role;
import com.fullstack.ticketflow.role.RoleRepository;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.user.User;
import com.fullstack.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var userDetails = (AppUserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");
        return new LoginResponse(jwtService.generateToken(userDetails), userDetails.getId(), role);
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered");
        }
        
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Default CLIENT role not found"));
        
        User user = User.builder()
                .email(request.email())
                .pwdHash(passwordEncoder.encode(request.password()))
                .role(role)
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                role.getName()
        );
    }
}
