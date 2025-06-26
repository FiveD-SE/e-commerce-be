package com.pm.productservice.service;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;

import java.util.UUID;

public interface CategoryService {
    CollectionResponse<CategoryDto> findAll();
    CategoryDto findById(UUID id);
    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(UUID id, CategoryDto categoryDto);
    void deleteById(UUID id);
}