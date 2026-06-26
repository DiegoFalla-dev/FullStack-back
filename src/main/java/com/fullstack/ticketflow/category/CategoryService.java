package com.fullstack.ticketflow.category;

import com.fullstack.ticketflow.category.dto.CategoryRequest;
import com.fullstack.ticketflow.category.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<CategoryResponse> list(Pageable pageable);
    List<CategoryResponse> listAll();
    CategoryResponse getById(Short id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Short id, CategoryRequest request);
    void delete(Short id);
}
