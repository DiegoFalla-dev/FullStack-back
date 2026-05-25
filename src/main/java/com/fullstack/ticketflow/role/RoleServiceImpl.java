package com.fullstack.ticketflow.role;

import com.fullstack.ticketflow.role.dto.RoleRequest;
import com.fullstack.ticketflow.role.dto.RoleResponse;
import com.fullstack.ticketflow.shared.exception.BusinessException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<RoleResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public RoleResponse getById(Short id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    @Transactional
    @Override
    public RoleResponse create(RoleRequest request) {
        if (repository.existsByName(request.name())) {
            throw new BusinessException("Role already exists");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    @Override
    public RoleResponse update(Short id, RoleRequest request) {
        Role role = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        if (!role.getName().equals(request.name()) && repository.existsByName(request.name())) {
            throw new BusinessException("Role already exists");
        }
        role.setName(request.name());
        return mapper.toResponse(repository.save(role));
    }

    @Transactional
    @Override
    public void delete(Short id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found");
        }
        repository.deleteById(id);
    }
}
