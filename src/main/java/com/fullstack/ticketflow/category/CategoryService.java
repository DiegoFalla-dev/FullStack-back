package com.fullstack.ticketflow.category;

import com.fullstack.ticketflow.category.dto.CategoryRequest;
import com.fullstack.ticketflow.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> listAll();
    CategoryResponse getById(Short id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Short id, CategoryRequest request);
    void delete(Short id);
}
