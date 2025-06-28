package com.pm.productservice.service.impl;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.exception.CategoryNotFoundException;
import com.pm.productservice.exception.DuplicateResourceException;
import com.pm.productservice.mapper.CategoryMapper;
import com.pm.productservice.model.Category;
import com.pm.productservice.model.CategoryStatus;
import com.pm.productservice.model.ProductStatus;
import com.pm.productservice.repository.CategoryRepository;
import com.pm.productservice.repository.ProductRepository;
import com.pm.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findAll() {
        log.info("Fetching all categories");
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements(categories.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findAll(Pageable pageable) {
        log.info("Fetching categories with pagination: {}", pageable);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        List<CategoryDto> categories = categoriesPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements((int) categoriesPage.getTotalElements())
                .totalElements((int) categoriesPage.getTotalElements())
                .page(categoriesPage.getNumber())
                .size(categoriesPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findByStatus(CategoryStatus status, Pageable pageable) {
        log.info("Fetching categories by status: {} with pagination: {}", status, pageable);
        Page<Category> categoriesPage = categoryRepository.findByStatus(status, pageable);
        List<CategoryDto> categories = categoriesPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements((int) categoriesPage.getTotalElements())
                .totalElements((int) categoriesPage.getTotalElements())
                .page(categoriesPage.getNumber())
                .size(categoriesPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findRootCategories(CategoryStatus status, Pageable pageable) {
        log.info("Fetching root categories with status: {} and pagination: {}", status, pageable);
        List<Category> rootCategories;
        if (status != null) {
            rootCategories = categoryRepository.findByParentIdIsNullAndStatus(status);
        } else {
            rootCategories = categoryRepository.findByParentIdIsNull();
        }
        
        List<CategoryDto> categories = rootCategories.stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements(categories.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findChildren(UUID parentId, CategoryStatus status, Pageable pageable) {
        log.info("Fetching children for parent ID: {} with status: {} and pagination: {}", parentId, status, pageable);
        List<Category> children;
        if (status != null) {
            children = categoryRepository.findByParentIdAndStatus(parentId, status);
        } else {
            children = categoryRepository.findByParentId(parentId);
        }
        
        List<CategoryDto> categories = children.stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements(categories.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> findWithFilters(String search, CategoryStatus status, UUID parentId, Pageable pageable) {
        log.info("Fetching categories with filters - search: {}, status: {}, parentId: {}, pagination: {}", 
                search, status, parentId, pageable);
        Page<Category> categoriesPage = categoryRepository.findWithFilters(search, status, parentId, pageable);
        List<CategoryDto> categories = categoriesPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<CategoryDto>builder()
                .data(categories)
                .totalElements((int) categoriesPage.getTotalElements())
                .totalElements((int) categoriesPage.getTotalElements())
                .page(categoriesPage.getNumber())
                .size(categoriesPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CategoryDto> getCategoryTree(CategoryStatus status) {
        log.info("Building category tree with status filter: {}", status);
        
        // Get all categories
        List<Category> allCategories;
        if (status != null) {
            allCategories = categoryRepository.findByStatus(status);
        } else {
            allCategories = categoryRepository.findAll();
        }
        
        // Convert to DTOs with extended info
        List<CategoryDto> categoryDtos = allCategories.stream()
                .map(this::toDtoWithExtendedInfo)
                .collect(Collectors.toList());
        
        // Build tree structure
        Map<UUID, List<CategoryDto>> parentChildMap = categoryDtos.stream()
                .filter(dto -> dto.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::getParentId));
        
        // Set children for each category
        categoryDtos.forEach(dto -> {
            List<CategoryDto> children = parentChildMap.get(dto.getId());
            if (children != null) {
                dto.setChildren(children);
            }
        });
        
        // Return only root categories (the tree structure is in their children)
        List<CategoryDto> rootCategories = categoryDtos.stream()
                .filter(dto -> dto.getParentId() == null)
                .collect(Collectors.toList());
        
        return CollectionResponse.<CategoryDto>builder()
                .data(rootCategories)
                .totalElements(rootCategories.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(UUID id) {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toDtoWithExtendedInfo(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("Saving new category: {}", categoryDto.getName());
        
        // Check for duplicate name
        if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw DuplicateResourceException.forCategoryName(categoryDto.getName());
        }
        
        Category category = categoryMapper.toEntity(categoryDto);
        category.setStatus(CategoryStatus.ACTIVE);
        Category savedCategory = categoryRepository.save(category);
        return toDtoWithExtendedInfo(savedCategory);
    }

    @Override
    public CategoryDto update(UUID id, CategoryDto categoryDto) {
        log.info("Updating category with ID: {}", id);
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        // Check for duplicate name (excluding current category)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryDto.getName(), id)) {
            throw DuplicateResourceException.forCategoryName(categoryDto.getName());
        }
        
        Category category = categoryMapper.toEntity(categoryDto);
        category.setId(id);
        Category updatedCategory = categoryRepository.save(category);
        return toDtoWithExtendedInfo(updatedCategory);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDto toDtoWithExtendedInfo(Category category) {
        CategoryDto dto = categoryMapper.toDTO(category);
        
        // Add parent name
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId()).ifPresent(parent -> 
                dto.setParentName(parent.getName())
            );
        }
        
        // Add product count
        Long productCount = productRepository.countByCategoryIdAndStatus(category.getId(), ProductStatus.active);
        dto.setProductCount(productCount != null ? productCount.intValue() : 0);
        
        return dto;
    }
}