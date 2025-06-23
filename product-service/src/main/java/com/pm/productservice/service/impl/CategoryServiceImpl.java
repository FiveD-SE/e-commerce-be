package com.pm.productservice.service.impl;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.mapper.CategoryMapper;
import com.pm.productservice.model.Category;
import com.pm.productservice.model.CategoryStatus;
import com.pm.productservice.repository.CategoryRepository;
import com.pm.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findAll() {
        log.info("Fetching all categories");
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements(categories.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(UUID id) {
        log.info("Fetching category with ID: {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("Saving new category: {}", categoryDto.getName());
        Category category = categoryMapper.toEntity(categoryDto);
        category.setStatus(CategoryStatus.ACTIVE);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public CategoryDto update(UUID id, CategoryDto categoryDto) {
        log.info("Updating category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        Category category = categoryMapper.toEntity(categoryDto);
        category.setId(id);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}