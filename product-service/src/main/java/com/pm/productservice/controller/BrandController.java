package com.pm.productservice.controller;

import com.pm.productservice.dto.BrandDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.BrandStatus;
import com.pm.productservice.service.BrandService;
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

@Slf4j
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Management", description = "APIs for managing brands and their information")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Get brands with optional filtering and pagination")
    public ResponseEntity<CollectionResponse<BrandDto>> findAll(
            @Parameter(description = "Search term for brand name or description")
            @RequestParam(value = "search", required = false) String search,
            
            @Parameter(description = "Filter by brand status")
            @RequestParam(value = "status", required = false) BrandStatus status,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Sort field")
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        
        log.info("Fetching brands with filters - search: {}, status: {}, page: {}, size: {}", 
                search, status, page, size);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        CollectionResponse<BrandDto> response = brandService.findWithFilters(search, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a brand by ID")
    public ResponseEntity<BrandDto> findById(
            @PathVariable @NotNull(message = "Brand ID must not be null") @Valid UUID id) {
        log.info("Fetching brand with ID: {}", id);
        return ResponseEntity.ok(brandService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new brand")
    public ResponseEntity<BrandDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid BrandDto brandDto) {
        log.info("Saving new brand: {}", brandDto.getName());
        BrandDto savedBrand = brandService.save(brandDto);
        return ResponseEntity.created(URI.create("/api/brands/" + savedBrand.getId())).body(savedBrand);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a brand by ID")
    public ResponseEntity<BrandDto> update(
            @PathVariable @NotNull(message = "Brand ID must not be null") @Valid UUID id,
            @RequestBody @NotNull(message = "Input must not be null") @Valid BrandDto brandDto) {
        log.info("Updating brand with ID: {}", id);
        return ResponseEntity.ok(brandService.update(id, brandDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a brand by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Brand ID must not be null") @Valid UUID id) {
        log.info("Deleting brand with ID: {}", id);
        brandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 