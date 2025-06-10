package com.pm.productservice.controller;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Operation(summary = "Get all categories")
    public ResponseEntity<CollectionResponse<CategoryDto>> findAll() {
        log.info("Fetching all categories");
        return ResponseEntity.ok(categoryService.findAll());
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