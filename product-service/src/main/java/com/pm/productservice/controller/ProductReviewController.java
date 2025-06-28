package com.pm.productservice.controller;

import com.pm.productservice.dto.ProductReviewDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.ReviewStatus;
import com.pm.productservice.service.ProductReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Review Management", description = "APIs for managing product reviews")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get product reviews with optional filtering and pagination")
    public ResponseEntity<CollectionResponse<ProductReviewDto>> findAll(
            @Parameter(description = "Filter by product ID")
            @RequestParam(value = "productId", required = false) UUID productId,
            
            @Parameter(description = "Filter by user ID")
            @RequestParam(value = "userId", required = false) UUID userId,
            
            @Parameter(description = "Filter by rating")
            @RequestParam(value = "rating", required = false) Integer rating,
            
            @Parameter(description = "Filter by review status")
            @RequestParam(value = "status", required = false) ReviewStatus status,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Sort field")
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        
        log.info("Fetching reviews with filters - productId: {}, userId: {}, rating: {}, status: {}, page: {}, size: {}", 
                productId, userId, rating, status, page, size);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        CollectionResponse<ProductReviewDto> response = reviewService.findWithFilters(productId, userId, rating, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get reviews for a specific product")
    public ResponseEntity<CollectionResponse<ProductReviewDto>> findByProductId(
            @PathVariable @NotNull(message = "Product ID must not be null") UUID productId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("Fetching reviews for product ID: {}, page: {}, size: {}", productId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(reviewService.findByProductId(productId, pageable));
    }

    @GetMapping("/products/{productId}/stats")
    @Operation(summary = "Get review statistics for a product")
    public ResponseEntity<ReviewStats> getProductReviewStats(
            @PathVariable @NotNull(message = "Product ID must not be null") UUID productId) {
        
        log.info("Fetching review stats for product ID: {}", productId);
        Double averageRating = reviewService.getAverageRating(productId);
        Long reviewCount = reviewService.getReviewCount(productId);
        
        ReviewStats stats = ReviewStats.builder()
                .averageRating(averageRating != null ? averageRating : 0.0)
                .reviewCount(reviewCount != null ? reviewCount : 0L)
                .build();
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product review by ID")
    public ResponseEntity<ProductReviewDto> findById(
            @PathVariable @NotNull(message = "Review ID must not be null") @Valid UUID id) {
        log.info("Fetching review with ID: {}", id);
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new product review")
    public ResponseEntity<ProductReviewDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid ProductReviewDto reviewDto) {
        log.info("Saving new review for product: {} by user: {}", reviewDto.getProductId(), reviewDto.getUserId());
        ProductReviewDto savedReview = reviewService.save(reviewDto);
        return ResponseEntity.created(URI.create("/api/reviews/" + savedReview.getId())).body(savedReview);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product review by ID")
    public ResponseEntity<ProductReviewDto> update(
            @PathVariable @NotNull(message = "Review ID must not be null") @Valid UUID id,
            @RequestBody @NotNull(message = "Input must not be null") @Valid ProductReviewDto reviewDto) {
        log.info("Updating review with ID: {}", id);
        return ResponseEntity.ok(reviewService.update(id, reviewDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product review by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Review ID must not be null") @Valid UUID id) {
        log.info("Deleting review with ID: {}", id);
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Inner class for review statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReviewStats {
        private Double averageRating;
        private Long reviewCount;
    }
} 