package com.pm.productservice.controller;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.ProductStatus;
import com.pm.productservice.service.ProductService;
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

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products and their information")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get products with optional filtering and pagination")
    public ResponseEntity<CollectionResponse<ProductDto>> findAll(
            @Parameter(description = "Search term for product name, description, or SKU")
            @RequestParam(value = "search", required = false) String search,
            
            @Parameter(description = "Filter by category ID")
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            
            @Parameter(description = "Filter by brand ID")
            @RequestParam(value = "brandId", required = false) UUID brandId,
            
            @Parameter(description = "Filter by product status")
            @RequestParam(value = "status", required = false) ProductStatus status,
            
            @Parameter(description = "Minimum price filter")
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price filter")
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Sort field (name, price, salesCount, createdAt, updatedAt)")
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        
        log.info("Fetching products with filters - search: {}, categoryId: {}, brandId: {}, status: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}", 
                search, categoryId, brandId, status, minPrice, maxPrice, page, size);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        CollectionResponse<ProductDto> response = productService.findWithFilters(search, categoryId, brandId, status, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<CollectionResponse<ProductDto>> findByCategory(
            @PathVariable @NotNull(message = "Category ID must not be null") UUID categoryId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("Fetching products for category ID: {}, page: {}, size: {}", categoryId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(productService.findByCategory(categoryId, pageable));
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Get products by brand")
    public ResponseEntity<CollectionResponse<ProductDto>> findByBrand(
            @PathVariable @NotNull(message = "Brand ID must not be null") UUID brandId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("Fetching products for brand ID: {}, page: {}, size: {}", brandId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(productService.findByBrand(brandId, pageable));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get a product by SKU")
    public ResponseEntity<ProductDto> findBySku(
            @PathVariable @NotNull(message = "SKU must not be null") String sku) {
        log.info("Fetching product with SKU: {}", sku);
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductDto> findById(
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid UUID id) {
        log.info("Fetching product with ID: {}", id);
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid ProductDto productDto) {
        log.info("Saving new product: {}", productDto.getName());
        ProductDto savedProduct = productService.save(productDto);
        return ResponseEntity.created(URI.create("/api/products/" + savedProduct.getId())).body(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product by ID")
    public ResponseEntity<ProductDto> update(
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid UUID id,
            @RequestBody @NotNull(message = "Input must not be null") @Valid ProductDto productDto) {
        log.info("Updating product with ID: {}", id);
        return ResponseEntity.ok(productService.update(id, productDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid UUID id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Get top selling products")
    public ResponseEntity<CollectionResponse<ProductDto>> findTopSellingProducts(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("Fetching top selling products, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findTopSellingProducts(pageable));
    }

    @PutMapping("/{id}/sales")
    @Operation(summary = "Update sales count for a product")
    public ResponseEntity<Void> updateSalesCount(
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid UUID id,
            @RequestParam @NotNull(message = "Additional sales must not be null") Integer additionalSales) {
        log.info("Updating sales count for product ID: {} with additional sales: {}", id, additionalSales);
        productService.updateSalesCount(id, additionalSales);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sku/{sku}/sales")
    @Operation(summary = "Update sales count for a product by SKU")
    public ResponseEntity<Void> updateSalesCountBySku(
            @PathVariable @NotNull(message = "SKU must not be null") String sku,
            @RequestParam @NotNull(message = "Additional sales must not be null") Integer additionalSales) {
        log.info("Updating sales count for product SKU: {} with additional sales: {}", sku, additionalSales);
        productService.updateSalesCount(sku, additionalSales);
        return ResponseEntity.ok().build();
    }
}