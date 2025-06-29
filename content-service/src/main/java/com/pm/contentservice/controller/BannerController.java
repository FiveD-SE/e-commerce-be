package com.pm.contentservice.controller;

import com.pm.contentservice.dto.BannerDto;
import com.pm.contentservice.dto.CreateBannerRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import com.pm.contentservice.service.BannerService;
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

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banner Management", description = "APIs for managing banners")
public class BannerController {

    private final BannerService bannerService;

    // ==================== Banner CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create a new banner", description = "Creates a new banner with the provided information")
    public ResponseEntity<BannerDto> createBanner(@Valid @RequestBody CreateBannerRequest request) {
        log.info("Creating banner with name: {}", request.getName());
        BannerDto createdBanner = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBanner);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get banner by ID", description = "Retrieves a banner by its ID")
    public ResponseEntity<BannerDto> getBannerById(@PathVariable Long id) {
        log.info("Fetching banner by ID: {}", id);
        BannerDto banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(banner);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update banner", description = "Updates an existing banner")
    public ResponseEntity<BannerDto> updateBanner(@PathVariable Long id, @Valid @RequestBody BannerDto bannerDto) {
        log.info("Updating banner ID: {}", id);
        BannerDto updatedBanner = bannerService.updateBanner(id, bannerDto);
        return ResponseEntity.ok(updatedBanner);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete banner", description = "Deletes a banner by its ID")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        log.info("Deleting banner ID: {}", id);
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Banner Listing and Filtering ====================

    @GetMapping
    @Operation(summary = "Get all banners", description = "Retrieves all banners with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getAllBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getAllBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/label/{label}")
    @Operation(summary = "Get banners by label", description = "Retrieves banners by label with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByLabel(
            @PathVariable String label,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getBannersByLabel(label, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/position/{position}")
    @Operation(summary = "Get banners by position", description = "Retrieves banners by position with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByPosition(
            @PathVariable String position,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getBannersByPosition(position, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get banners by type", description = "Retrieves banners by type with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByType(
            @PathVariable String type,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getBannersByType(type, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/created-by/{createdBy}")
    @Operation(summary = "Get banners by creator", description = "Retrieves banners by creator with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByCreatedBy(
            @PathVariable Integer createdBy,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getBannersByCreatedBy(createdBy, pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Active Banners ====================

    @GetMapping("/active")
    @Operation(summary = "Get active banners", description = "Retrieves all active banners with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getActiveBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getActiveBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/active/position/{position}")
    @Operation(summary = "Get active banners by position", description = "Retrieves active banners by position")
    public ResponseEntity<List<BannerDto>> getActiveBannersByPosition(@PathVariable String position) {
        List<BannerDto> banners = bannerService.getActiveBannersByPosition(position);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/active/position/{position}/paginated")
    @Operation(summary = "Get active banners by position with pagination", description = "Retrieves active banners by position with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getActiveBannersByPositionPaginated(
            @PathVariable String position,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getActiveBannersByPosition(position, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured active banners", description = "Retrieves all featured active banners with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getFeaturedActiveBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getFeaturedActiveBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Scheduled and Expired Banners ====================

    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled banners", description = "Retrieves all scheduled banners with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getScheduledBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getScheduledBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired banners", description = "Retrieves all expired banners with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> getExpiredBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "endDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BannerDto> banners = bannerService.getExpiredBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Banner Search ====================

    @GetMapping("/search")
    @Operation(summary = "Search banners", description = "Searches banners by term with pagination")
    public ResponseEntity<CollectionResponse<BannerDto>> searchBanners(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BannerDto> banners = bannerService.searchBanners(q, pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Banner Actions ====================

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate banner", description = "Activates a banner")
    public ResponseEntity<BannerDto> activateBanner(@PathVariable Long id) {
        log.info("Activating banner ID: {}", id);
        BannerDto banner = bannerService.activateBanner(id);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate banner", description = "Deactivates a banner")
    public ResponseEntity<BannerDto> deactivateBanner(@PathVariable Long id) {
        log.info("Deactivating banner ID: {}", id);
        BannerDto banner = bannerService.deactivateBanner(id);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/{id}/feature")
    @Operation(summary = "Feature banner", description = "Features a banner")
    public ResponseEntity<BannerDto> featureBanner(@PathVariable Long id) {
        log.info("Featuring banner ID: {}", id);
        BannerDto banner = bannerService.featureBanner(id);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/{id}/unfeature")
    @Operation(summary = "Unfeature banner", description = "Unfeatures a banner")
    public ResponseEntity<BannerDto> unfeatureBanner(@PathVariable Long id) {
        log.info("Unfeaturing banner ID: {}", id);
        BannerDto banner = bannerService.unfeatureBanner(id);
        return ResponseEntity.ok(banner);
    }

    // ==================== Banner Interactions ====================

    @PostMapping("/{id}/click")
    @Operation(summary = "Track banner click", description = "Increments the click count of a banner")
    public ResponseEntity<BannerDto> trackClick(@PathVariable Long id) {
        BannerDto banner = bannerService.incrementClickCount(id);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Track banner view", description = "Increments the view count of a banner")
    public ResponseEntity<BannerDto> trackView(@PathVariable Long id) {
        BannerDto banner = bannerService.incrementViewCount(id);
        return ResponseEntity.ok(banner);
    }

    // ==================== Banner Metadata ====================

    @GetMapping("/positions")
    @Operation(summary = "Get all positions", description = "Retrieves all banner positions")
    public ResponseEntity<List<String>> getAllPositions() {
        List<String> positions = bannerService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/types")
    @Operation(summary = "Get all types", description = "Retrieves all banner types")
    public ResponseEntity<List<String>> getAllTypes() {
        List<String> types = bannerService.getAllTypes();
        return ResponseEntity.ok(types);
    }

    // ==================== Popular Banners ====================

    @GetMapping("/popular")
    @Operation(summary = "Get popular banners", description = "Retrieves popular banners ordered by click count")
    public ResponseEntity<CollectionResponse<BannerDto>> getPopularBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<BannerDto> banners = bannerService.getPopularBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/most-viewed")
    @Operation(summary = "Get most viewed banners", description = "Retrieves most viewed banners ordered by view count")
    public ResponseEntity<CollectionResponse<BannerDto>> getMostViewedBanners(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<BannerDto> banners = bannerService.getMostViewedBanners(pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Date Range Queries ====================

    @GetMapping("/date-range")
    @Operation(summary = "Get banners by date range", description = "Retrieves banners created within a date range")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BannerDto> banners = bannerService.getBannersByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/start-date-range")
    @Operation(summary = "Get banners by start date range", description = "Retrieves banners with start date within a range")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByStartDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BannerDto> banners = bannerService.getBannersByStartDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/end-date-range")
    @Operation(summary = "Get banners by end date range", description = "Retrieves banners with end date within a range")
    public ResponseEntity<CollectionResponse<BannerDto>> getBannersByEndDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "endDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BannerDto> banners = bannerService.getBannersByEndDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(banners);
    }

    // ==================== Scheduled Tasks ====================

    @PostMapping("/tasks/activate-scheduled")
    @Operation(summary = "Activate scheduled banners", description = "Manually triggers activation of scheduled banners")
    public ResponseEntity<Void> activateScheduledBanners() {
        log.info("Manually activating scheduled banners");
        bannerService.activateScheduledBanners();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/deactivate-expired")
    @Operation(summary = "Deactivate expired banners", description = "Manually triggers deactivation of expired banners")
    public ResponseEntity<Void> deactivateExpiredBanners() {
        log.info("Manually deactivating expired banners");
        bannerService.deactivateExpiredBanners();
        return ResponseEntity.ok().build();
    }
}
