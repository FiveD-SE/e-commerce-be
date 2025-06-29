package com.pm.promotionservice.service;

import com.pm.promotionservice.dto.*;
import com.pm.promotionservice.dto.response.collection.CollectionResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PromotionService {
    
    // Promotion CRUD Operations
    PromotionDto createPromotion(CreatePromotionRequest request);
    PromotionDto getPromotionById(Long id);
    PromotionDto getPromotionByCode(String code);
    PromotionDto updatePromotion(Long id, PromotionDto promotionDto);
    void deletePromotion(Long id);
    
    // Promotion Listing and Filtering
    CollectionResponse<PromotionDto> getAllPromotions(Pageable pageable);
    CollectionResponse<PromotionDto> getActivePromotions(Pageable pageable);
    CollectionResponse<PromotionDto> getFeaturedPromotions(Pageable pageable);
    CollectionResponse<PromotionDto> getPromotionsByType(String type, Pageable pageable);
    CollectionResponse<PromotionDto> getScheduledPromotions(Pageable pageable);
    CollectionResponse<PromotionDto> getExpiredPromotions(Pageable pageable);
    CollectionResponse<PromotionDto> getOutOfStockPromotions(Pageable pageable);
    
    // Promotion Search
    CollectionResponse<PromotionDto> searchPromotions(String searchTerm, Pageable pageable);
    CollectionResponse<PromotionDto> searchActivePromotions(String searchTerm, Pageable pageable);
    List<PromotionDto> fullTextSearchPromotions(String searchTerm);
    
    // Promotion Application (Main Integration with Payment)
    PromotionApplicationResult applyPromotion(ApplyPromotionRequest request);
    PromotionApplicationResult validatePromotion(String promotionCode, Integer userId, BigDecimal orderAmount);
    List<PromotionDto> getAutoApplyPromotions(BigDecimal orderAmount, Integer userId);
    
    // Promotion Usage Management
    void cancelPromotionUsage(Long usageId);
    void refundPromotionUsage(Long usageId);
    void restorePromotionStock(Long promotionId);
    
    // Promotion Actions
    PromotionDto activatePromotion(Long id);
    PromotionDto deactivatePromotion(Long id);
    PromotionDto featurePromotion(Long id);
    PromotionDto unfeaturePromotion(Long id);
    
    // Promotion Analytics
    long countActivePromotions();
    long countFeaturedPromotions();
    long countPromotionsByType(String type);
    long countOutOfStockPromotions();
    long countExpiredPromotions();
    Long getTotalUsageCount();
    Double getAverageUsageCount();
    
    // User Analytics
    long countUserPromotionUsage(Integer userId, Long promotionId);
    BigDecimal getTotalDiscountByUser(Integer userId);
    long countUserTotalUsage(Integer userId);
    
    // Promotion Analytics by ID
    long countPromotionUsage(Long promotionId);
    BigDecimal getTotalDiscountByPromotion(Long promotionId);
    BigDecimal getAverageDiscountByPromotion(Long promotionId);
    long countUniqueUsersByPromotion(Long promotionId);
    
    // Date Range Queries
    CollectionResponse<PromotionDto> getPromotionsByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<PromotionDto> getPromotionsByStartDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<PromotionDto> getPromotionsByEndDateRange(Instant startDate, Instant endDate, Pageable pageable);
    
    // Stock Management
    CollectionResponse<PromotionDto> getLowStockPromotions(Integer threshold, Pageable pageable);
    void updatePromotionStock(Long promotionId, Integer newStock);
    
    // Scheduled Tasks
    void activateScheduledPromotions();
    void deactivateExpiredPromotions();
    void deactivateOutOfStockPromotions();
    void cleanupExpiredPromotions();
    
    // Utility Methods
    String generatePromotionCode(String prefix);
    boolean isPromotionCodeUnique(String code);
    BigDecimal calculateDiscount(String promotionCode, BigDecimal orderAmount);
}
