package com.pm.productservice.controller;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.CategoryStatus;
import com.pm.productservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing categories and their information")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get categories with optional filtering and pagination")
    public ResponseEntity<CollectionResponse<CategoryDto>> findAll(
            @Parameter(description = "Search term for category name")
            @RequestParam(value = "search", required = false) String search,
            
            @Parameter(description = "Filter by category status")
            @RequestParam(value = "status", required = false) CategoryStatus status,
            
            @Parameter(description = "Filter by parent category ID")
            @RequestParam(value = "parentId", required = false) UUID parentId,
            
            @Parameter(description = "Get only root categories (no parent)")
            @RequestParam(value = "rootOnly", defaultValue = "false") boolean rootOnly,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Sort field")
            @RequestParam(value = "sort", defaultValue = "displayOrder") String sort,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        
        log.info("Fetching categories with filters - search: {}, status: {}, parentId: {}, rootOnly: {}, page: {}, size: {}", 
                search, status, parentId, rootOnly, page, size);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        CollectionResponse<CategoryDto> response;
        if (rootOnly) {
            response = categoryService.findRootCategories(status, pageable);
        } else {
            response = categoryService.findWithFilters(search, status, parentId, pageable);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tree")
    @Operation(summary = "Get categories in hierarchical tree structure")
    public ResponseEntity<CollectionResponse<CategoryDto>> getCategoryTree(
            @Parameter(description = "Filter by status")
            @RequestParam(value = "status", required = false) CategoryStatus status) {
        log.info("Fetching category tree with status filter: {}", status);
        return ResponseEntity.ok(categoryService.getCategoryTree(status));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get child categories of a specific category")
    public ResponseEntity<CollectionResponse<CategoryDto>> getChildren(
            @PathVariable @NotNull(message = "Category ID must not be null") UUID id,
            @Parameter(description = "Filter by status")
            @RequestParam(value = "status", required = false) CategoryStatus status,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("Fetching children for category ID: {}, status: {}, page: {}, size: {}", id, status, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "displayOrder"));
        return ResponseEntity.ok(categoryService.findChildren(id, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public ResponseEntity<CategoryDto> findById(
            @PathVariable @NotNull(message = "Category ID must not be null") @Valid UUID id) {
        log.info("Fetching category with ID: {}", id);
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid CategoryDto categoryDto) {
        log.info("Saving new category: {}", categoryDto.getName());
        CategoryDto savedCategory = categoryService.save(categoryDto);
        return ResponseEntity.created(URI.create("/api/categories/" + savedCategory.getId())).body(savedCategory);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category by ID")
    public ResponseEntity<CategoryDto> update(
            @PathVariable @NotNull(message = "Category ID must not be null") @Valid UUID id,
            @RequestBody @NotNull(message = "Input must not be null") @Valid CategoryDto categoryDto) {
        log.info("Updating category with ID: {}", id);
        return ResponseEntity.ok(categoryService.update(id, categoryDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Category ID must not be null") @Valid UUID id) {
        log.info("Deleting category with ID: {}", id);
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}