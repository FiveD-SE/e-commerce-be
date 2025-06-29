package com.pm.contentservice.service;

import com.pm.contentservice.dto.BannerDto;
import com.pm.contentservice.dto.CreateBannerRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface BannerService {
    
    // Banner CRUD Operations
    BannerDto createBanner(CreateBannerRequest request);
    BannerDto getBannerById(Long id);
    BannerDto updateBanner(Long id, BannerDto bannerDto);
    void deleteBanner(Long id);
    
    // Banner Listing and Filtering
    CollectionResponse<BannerDto> getAllBanners(Pageable pageable);
    CollectionResponse<BannerDto> getBannersByLabel(String label, Pageable pageable);
    CollectionResponse<BannerDto> getBannersByPosition(String position, Pageable pageable);
    CollectionResponse<BannerDto> getBannersByType(String type, Pageable pageable);
    CollectionResponse<BannerDto> getBannersByCreatedBy(Integer createdBy, Pageable pageable);
    
    // Active Banners
    CollectionResponse<BannerDto> getActiveBanners(Pageable pageable);
    List<BannerDto> getActiveBannersByPosition(String position);
    CollectionResponse<BannerDto> getActiveBannersByPosition(String position, Pageable pageable);
    CollectionResponse<BannerDto> getFeaturedActiveBanners(Pageable pageable);
    
    // Scheduled and Expired Banners
    CollectionResponse<BannerDto> getScheduledBanners(Pageable pageable);
    CollectionResponse<BannerDto> getExpiredBanners(Pageable pageable);
    
    // Banner Search
    CollectionResponse<BannerDto> searchBanners(String searchTerm, Pageable pageable);
    
    // Banner Actions
    BannerDto activateBanner(Long id);
    BannerDto deactivateBanner(Long id);
    BannerDto featureBanner(Long id);
    BannerDto unfeatureBanner(Long id);
    
    // Banner Interactions
    BannerDto incrementClickCount(Long id);
    BannerDto incrementViewCount(Long id);
    
    // Banner Analytics
    long countActiveBanners();
    long countFeaturedBanners();
    long countBannersByPosition(String position);
    long countBannersByType(String type);
    long countActiveBannersByPosition(String position);
    Long getTotalClicks();
    Long getTotalViews();
    Double getAverageClicks();
    Double getAverageViews();
    
    // Banner Metadata
    List<String> getAllPositions();
    List<String> getAllTypes();
    
    // Date Range Queries
    CollectionResponse<BannerDto> getBannersByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<BannerDto> getBannersByStartDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<BannerDto> getBannersByEndDateRange(Instant startDate, Instant endDate, Pageable pageable);
    
    // Popular Banners
    CollectionResponse<BannerDto> getPopularBanners(Pageable pageable);
    CollectionResponse<BannerDto> getMostViewedBanners(Pageable pageable);
    
    // Scheduled Tasks
    void activateScheduledBanners();
    void deactivateExpiredBanners();
}
