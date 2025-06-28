package com.pm.productservice.service;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.CategoryStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    CollectionResponse<CategoryDto> findAll();
    CollectionResponse<CategoryDto> findAll(Pageable pageable);
    CollectionResponse<CategoryDto> findByStatus(CategoryStatus status, Pageable pageable);
    CollectionResponse<CategoryDto> findRootCategories(CategoryStatus status, Pageable pageable);
    CollectionResponse<CategoryDto> findChildren(UUID parentId, CategoryStatus status, Pageable pageable);
    CollectionResponse<CategoryDto> findWithFilters(String search, CategoryStatus status, UUID parentId, Pageable pageable);
    CollectionResponse<CategoryDto> getCategoryTree(CategoryStatus status);
    CategoryDto findById(UUID id);
    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(UUID id, CategoryDto categoryDto);
    void deleteById(UUID id);
}