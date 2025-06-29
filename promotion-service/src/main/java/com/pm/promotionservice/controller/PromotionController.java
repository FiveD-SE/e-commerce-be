package com.pm.promotionservice.controller;

import com.pm.promotionservice.dto.*;
import com.pm.promotionservice.dto.response.collection.CollectionResponse;
import com.pm.promotionservice.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotion Management", description = "APIs for managing promotions and discount codes")
public class PromotionController {

    private final PromotionService promotionService;

    // ==================== Promotion CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create a new promotion", description = "Creates a new promotion with the provided information")
    public ResponseEntity<PromotionDto> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        log.info("Creating promotion with code: {}", request.getCode());
        PromotionDto createdPromotion = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promotion by ID", description = "Retrieves a promotion by its ID")
    public ResponseEntity<PromotionDto> getPromotionById(@PathVariable Long id) {
        log.info("Fetching promotion by ID: {}", id);
        PromotionDto promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get promotion by code", description = "Retrieves a promotion by its code")
    public ResponseEntity<PromotionDto> getPromotionByCode(@PathVariable String code) {
        log.info("Fetching promotion by code: {}", code);
        PromotionDto promotion = promotionService.getPromotionByCode(code);
        return ResponseEntity.ok(promotion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promotion", description = "Updates an existing promotion")
    public ResponseEntity<PromotionDto> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDto promotionDto) {
        log.info("Updating promotion ID: {}", id);
        PromotionDto updatedPromotion = promotionService.updatePromotion(id, promotionDto);
        return ResponseEntity.ok(updatedPromotion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete promotion", description = "Deletes a promotion by its ID")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        log.info("Deleting promotion ID: {}", id);
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Promotion Listing and Filtering ====================

    @GetMapping
    @Operation(summary = "Get all promotions", description = "Retrieves all promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getAllPromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getAllPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active promotions", description = "Retrieves all active promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getActivePromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getActivePromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured promotions", description = "Retrieves all featured active promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getFeaturedPromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getFeaturedPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get promotions by type", description = "Retrieves promotions by type with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getPromotionsByType(
            @PathVariable String type,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getPromotionsByType(type, pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled promotions", description = "Retrieves all scheduled promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getScheduledPromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getScheduledPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired promotions", description = "Retrieves all expired promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getExpiredPromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "endDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getExpiredPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Get out of stock promotions", description = "Retrieves all out of stock promotions with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> getOutOfStockPromotions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "updatedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.getOutOfStockPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    // ==================== Promotion Search ====================

    @GetMapping("/search")
    @Operation(summary = "Search promotions", description = "Searches promotions by term with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> searchPromotions(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.searchPromotions(q, pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/search/active")
    @Operation(summary = "Search active promotions", description = "Searches active promotions by term with pagination")
    public ResponseEntity<CollectionResponse<PromotionDto>> searchActivePromotions(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<PromotionDto> promotions = promotionService.searchActivePromotions(q, pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/search/fulltext")
    @Operation(summary = "Full-text search promotions", description = "Performs full-text search on promotions")
    public ResponseEntity<List<PromotionDto>> fullTextSearchPromotions(
            @Parameter(description = "Search term") @RequestParam String q) {
        
        List<PromotionDto> promotions = promotionService.fullTextSearchPromotions(q);
        return ResponseEntity.ok(promotions);
    }

    // ==================== Promotion Application (Main Integration with Payment) ====================

    @PostMapping("/apply")
    @Operation(summary = "Apply promotion to order", description = "Applies a promotion code to an order and calculates discount")
    public ResponseEntity<PromotionApplicationResult> applyPromotion(@Valid @RequestBody ApplyPromotionRequest request) {
        log.info("Applying promotion code: {} for user: {} on order: {}",
                request.getPromotionCode(), request.getUserId(), request.getOrderId());
        PromotionApplicationResult result = promotionService.applyPromotion(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate promotion code", description = "Validates a promotion code without applying it")
    public ResponseEntity<PromotionApplicationResult> validatePromotion(
            @Parameter(description = "Promotion code") @RequestParam String code,
            @Parameter(description = "User ID") @RequestParam Integer userId,
            @Parameter(description = "Order amount") @RequestParam BigDecimal orderAmount) {

        log.info("Validating promotion code: {} for user: {} with amount: {}", code, userId, orderAmount);
        PromotionApplicationResult result = promotionService.validatePromotion(code, userId, orderAmount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/auto-apply")
    @Operation(summary = "Get auto-apply promotions", description = "Retrieves promotions that can be auto-applied to an order")
    public ResponseEntity<List<PromotionDto>> getAutoApplyPromotions(
            @Parameter(description = "Order amount") @RequestParam BigDecimal orderAmount,
            @Parameter(description = "User ID") @RequestParam Integer userId) {

        log.info("Getting auto-apply promotions for user: {} with order amount: {}", userId, orderAmount);
        List<PromotionDto> promotions = promotionService.getAutoApplyPromotions(orderAmount, userId);
        return ResponseEntity.ok(promotions);
    }

    @PostMapping("/calculate-discount")
    @Operation(summary = "Calculate discount", description = "Calculates discount amount for a promotion code and order amount")
    public ResponseEntity<BigDecimal> calculateDiscount(
            @Parameter(description = "Promotion code") @RequestParam String code,
            @Parameter(description = "Order amount") @RequestParam BigDecimal orderAmount) {

        log.info("Calculating discount for code: {} with amount: {}", code, orderAmount);
        BigDecimal discount = promotionService.calculateDiscount(code, orderAmount);
        return ResponseEntity.ok(discount);
    }

    // ==================== Promotion Usage Management ====================

    @PostMapping("/usage/{usageId}/cancel")
    @Operation(summary = "Cancel promotion usage", description = "Cancels a promotion usage and restores stock")
    public ResponseEntity<Void> cancelPromotionUsage(@PathVariable Long usageId) {
        log.info("Cancelling promotion usage ID: {}", usageId);
        promotionService.cancelPromotionUsage(usageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/usage/{usageId}/refund")
    @Operation(summary = "Refund promotion usage", description = "Refunds a promotion usage and restores stock")
    public ResponseEntity<Void> refundPromotionUsage(@PathVariable Long usageId) {
        log.info("Refunding promotion usage ID: {}", usageId);
        promotionService.refundPromotionUsage(usageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restore-stock")
    @Operation(summary = "Restore promotion stock", description = "Restores one unit of stock to a promotion")
    public ResponseEntity<Void> restorePromotionStock(@PathVariable Long id) {
        log.info("Restoring stock for promotion ID: {}", id);
        promotionService.restorePromotionStock(id);
        return ResponseEntity.ok().build();
    }

    // ==================== Promotion Actions ====================

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate promotion", description = "Activates a promotion")
    public ResponseEntity<PromotionDto> activatePromotion(@PathVariable Long id) {
        log.info("Activating promotion ID: {}", id);
        PromotionDto promotion = promotionService.activatePromotion(id);
        return ResponseEntity.ok(promotion);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate promotion", description = "Deactivates a promotion")
    public ResponseEntity<PromotionDto> deactivatePromotion(@PathVariable Long id) {
        log.info("Deactivating promotion ID: {}", id);
        PromotionDto promotion = promotionService.deactivatePromotion(id);
        return ResponseEntity.ok(promotion);
    }

    @PostMapping("/{id}/feature")
    @Operation(summary = "Feature promotion", description = "Features a promotion")
    public ResponseEntity<PromotionDto> featurePromotion(@PathVariable Long id) {
        log.info("Featuring promotion ID: {}", id);
        PromotionDto promotion = promotionService.featurePromotion(id);
        return ResponseEntity.ok(promotion);
    }

    @PostMapping("/{id}/unfeature")
    @Operation(summary = "Unfeature promotion", description = "Unfeatures a promotion")
    public ResponseEntity<PromotionDto> unfeaturePromotion(@PathVariable Long id) {
        log.info("Unfeaturing promotion ID: {}", id);
        PromotionDto promotion = promotionService.unfeaturePromotion(id);
        return ResponseEntity.ok(promotion);
    }

    // ==================== Utility Endpoints ====================

    @PostMapping("/generate-code")
    @Operation(summary = "Generate promotion code", description = "Generates a unique promotion code")
    public ResponseEntity<String> generatePromotionCode(
            @Parameter(description = "Code prefix") @RequestParam(required = false) String prefix) {

        String code = promotionService.generatePromotionCode(prefix);
        return ResponseEntity.ok(code);
    }

    @GetMapping("/check-code")
    @Operation(summary = "Check code uniqueness", description = "Checks if a promotion code is unique")
    public ResponseEntity<Boolean> checkCodeUniqueness(
            @Parameter(description = "Promotion code") @RequestParam String code) {

        boolean isUnique = promotionService.isPromotionCodeUnique(code);
        return ResponseEntity.ok(isUnique);
    }

    // ==================== Date Range Queries ====================

    @GetMapping("/date-range")
    @Operation(summary = "Get promotions by date range", description = "Retrieves promotions created within a date range")
    public ResponseEntity<CollectionResponse<PromotionDto>> getPromotionsByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<PromotionDto> promotions = promotionService.getPromotionsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(promotions);
    }

    // ==================== Stock Management ====================

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock promotions", description = "Retrieves promotions with low stock")
    public ResponseEntity<CollectionResponse<PromotionDto>> getLowStockPromotions(
            @Parameter(description = "Stock threshold") @RequestParam(defaultValue = "10") Integer threshold,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<PromotionDto> promotions = promotionService.getLowStockPromotions(threshold, pageable);
        return ResponseEntity.ok(promotions);
    }

    @PostMapping("/{id}/update-stock")
    @Operation(summary = "Update promotion stock", description = "Updates the stock of a promotion")
    public ResponseEntity<Void> updatePromotionStock(
            @PathVariable Long id,
            @Parameter(description = "New stock amount") @RequestParam Integer stock) {

        log.info("Updating stock for promotion ID: {} to: {}", id, stock);
        promotionService.updatePromotionStock(id, stock);
        return ResponseEntity.ok().build();
    }

    // ==================== Scheduled Tasks ====================

    @PostMapping("/tasks/activate-scheduled")
    @Operation(summary = "Activate scheduled promotions", description = "Manually triggers activation of scheduled promotions")
    public ResponseEntity<Void> activateScheduledPromotions() {
        log.info("Manually activating scheduled promotions");
        promotionService.activateScheduledPromotions();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/deactivate-expired")
    @Operation(summary = "Deactivate expired promotions", description = "Manually triggers deactivation of expired promotions")
    public ResponseEntity<Void> deactivateExpiredPromotions() {
        log.info("Manually deactivating expired promotions");
        promotionService.deactivateExpiredPromotions();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/deactivate-out-of-stock")
    @Operation(summary = "Deactivate out of stock promotions", description = "Manually triggers deactivation of out of stock promotions")
    public ResponseEntity<Void> deactivateOutOfStockPromotions() {
        log.info("Manually deactivating out of stock promotions");
        promotionService.deactivateOutOfStockPromotions();
        return ResponseEntity.ok().build();
    }
}
