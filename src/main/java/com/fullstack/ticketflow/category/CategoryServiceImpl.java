package com.fullstack.ticketflow.category;

import com.fullstack.ticketflow.category.dto.CategoryRequest;
import com.fullstack.ticketflow.category.dto.CategoryResponse;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getCreatedAt());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> listAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse getById(Short id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    @Transactional
    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByName(request.name())) {
            throw new BusinessRuleException("La categoría ya existe");
        }
        Category entity = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    @Override
    public CategoryResponse update(Short id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        if (!category.getName().equals(request.name()) && repository.existsByName(request.name())) {
            throw new BusinessRuleException("La categoría ya existe");
        }
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(repository.save(category));
    }

    @Transactional
    @Override
    public void delete(Short id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        // Si hay eventos con esta categoría, la FK impedirá el borrado y se
        // traducirá como 409 (DataIntegrityViolationException) en el handler global.
        repository.deleteById(id);
    }
}
