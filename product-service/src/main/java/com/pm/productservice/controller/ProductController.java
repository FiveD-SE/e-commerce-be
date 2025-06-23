package com.pm.productservice.controller;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products and their information")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<CollectionResponse<ProductDto>> findAll() {
        log.info("Fetching all products");
        return ResponseEntity.ok(productService.findAll());
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
}