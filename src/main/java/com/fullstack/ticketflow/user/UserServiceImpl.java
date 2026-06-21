package com.fullstack.ticketflow.user;

import com.fullstack.ticketflow.role.Role;
import com.fullstack.ticketflow.role.RoleRepository;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.user.dto.PasswordChangeRequest;
import com.fullstack.ticketflow.user.dto.ProfileUpdateRequest;
import com.fullstack.ticketflow.user.dto.UserRequest;
import com.fullstack.ticketflow.user.dto.UserResponse;
import com.fullstack.ticketflow.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> list(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getByEmail(String email) {
        return repository.findByEmail(email)
                .filter(User::isActive)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public UserResponse create(UserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered");
        }
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        User entity = mapper.toEntity(request);
        entity.setRole(role);
        entity.setActive(true); // Solución: Fuerza la activación del usuario
        entity.setPwdHash(passwordEncoder.encode(request.password()));
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @Override
    public UserResponse update(String id, UserUpdateRequest request) {
        User user = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        mapper.updateFromDto(request, user);
        user.setRole(role);
        return mapper.toResponse(repository.save(user));
    }

    @Transactional
    @Override
    public UserResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = repository.findByEmail(email)
                .filter(User::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(request.fullName());
        return mapper.toResponse(repository.save(user));
    }

    @Transactional
    @Override
    public void changePassword(String id, PasswordChangeRequest request) {
        User user = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPwdHash(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
    }

    @Transactional
    @Override
    public void deactivate(String id) {
        User user = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        repository.save(user);
    }
}
