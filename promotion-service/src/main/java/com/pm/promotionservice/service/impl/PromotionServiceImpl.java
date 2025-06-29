package com.pm.promotionservice.service.impl;

import com.pm.promotionservice.dto.*;
import com.pm.promotionservice.dto.response.collection.CollectionResponse;
import com.pm.promotionservice.mapper.PromotionMapper;
import com.pm.promotionservice.model.Promotion;
import com.pm.promotionservice.model.PromotionUsage;
import com.pm.promotionservice.repository.PromotionRepository;
import com.pm.promotionservice.repository.PromotionUsageRepository;
import com.pm.promotionservice.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final PromotionMapper promotionMapper;

    // ==================== Promotion CRUD Operations ====================
    
    @Override
    @CacheEvict(value = "promotions", allEntries = true)
    public PromotionDto createPromotion(CreatePromotionRequest request) {
        log.info("Creating promotion with code: {}", request.getCode());
        
        // Check if code is unique
        if (!isPromotionCodeUnique(request.getCode())) {
            throw new RuntimeException("Promotion code already exists: " + request.getCode());
        }
        
        Promotion promotion = Promotion.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .type(request.getType())
                .percent(request.getPercent())
                .discountAmount(request.getDiscountAmount())
                .maxDiscount(request.getMaxDiscount())
                .minOrderAmount(request.getMinOrderAmount())
                .stock(request.getStock())
                .maxUsesPerUser(request.getMaxUsesPerUser())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .isStackable(request.getIsStackable() != null ? request.getIsStackable() : false)
                .applicableCategories(request.getApplicableCategories())
                .applicableProducts(request.getApplicableProducts())
                .applicableBrands(request.getApplicableBrands())
                .excludedCategories(request.getExcludedCategories())
                .excludedProducts(request.getExcludedProducts())
                .userGroups(request.getUserGroups())
                .firstTimeUserOnly(request.getFirstTimeUserOnly() != null ? request.getFirstTimeUserOnly() : false)
                .autoApply(request.getAutoApply() != null ? request.getAutoApply() : false)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .createdBy(request.getCreatedBy())
                .build();
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion created successfully with ID: {}", savedPromotion.getId());
        
        return promotionMapper.toDTO(savedPromotion);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promotions", key = "#id")
    public PromotionDto getPromotionById(Long id) {
        log.info("Fetching promotion by ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        return promotionMapper.toDTO(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promotions", key = "#code")
    public PromotionDto getPromotionByCode(String code) {
        log.info("Fetching promotion by code: {}", code);
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Promotion not found with code: " + code));
        return promotionMapper.toDTO(promotion);
    }

    @Override
    @CacheEvict(value = "promotions", allEntries = true)
    public PromotionDto updatePromotion(Long id, PromotionDto promotionDto) {
        log.info("Updating promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        
        // Check if code is unique (if changed)
        if (!promotion.getCode().equals(promotionDto.getCode()) && 
            !isPromotionCodeUnique(promotionDto.getCode())) {
            throw new RuntimeException("Promotion code already exists: " + promotionDto.getCode());
        }
        
        promotionMapper.updateEntityFromDTO(promotionDto, promotion);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        
        log.info("Promotion updated successfully: {}", id);
        return promotionMapper.toDTO(updatedPromotion);
    }

    @Override
    @CacheEvict(value = "promotions", allEntries = true)
    public void deletePromotion(Long id) {
        log.info("Deleting promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        
        // Check if promotion has been used
        long usageCount = promotionUsageRepository.countUsageByPromotionId(id);
        if (usageCount > 0) {
            log.warn("Cannot delete promotion {} as it has {} usage records", id, usageCount);
            throw new RuntimeException("Cannot delete promotion that has been used");
        }
        
        promotionRepository.delete(promotion);
        log.info("Promotion deleted successfully: {}", id);
    }

    // ==================== Promotion Listing and Filtering ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getAllPromotions(Pageable pageable) {
        log.info("Fetching all promotions with pagination: {}", pageable);
        Page<Promotion> promotionPage = promotionRepository.findAll(pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "activePromotions")
    public CollectionResponse<PromotionDto> getActivePromotions(Pageable pageable) {
        log.info("Fetching active promotions with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Promotion> promotionPage = promotionRepository.findActivePromotions(now, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getFeaturedPromotions(Pageable pageable) {
        log.info("Fetching featured promotions with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Promotion> promotionPage = promotionRepository.findFeaturedActivePromotions(now, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getPromotionsByType(String type, Pageable pageable) {
        log.info("Fetching promotions by type: {} with pagination: {}", type, pageable);
        Page<Promotion> promotionPage = promotionRepository.findByType(type, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getScheduledPromotions(Pageable pageable) {
        log.info("Fetching scheduled promotions with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Promotion> promotionPage = promotionRepository.findScheduledPromotions(now, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getExpiredPromotions(Pageable pageable) {
        log.info("Fetching expired promotions with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Promotion> promotionPage = promotionRepository.findExpiredPromotions(now, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(), 
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getOutOfStockPromotions(Pageable pageable) {
        log.info("Fetching out of stock promotions with pagination: {}", pageable);
        Page<Promotion> promotionPage = promotionRepository.findOutOfStockPromotions(pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    // ==================== Promotion Search ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> searchPromotions(String searchTerm, Pageable pageable) {
        log.info("Searching promotions with term: {} and pagination: {}", searchTerm, pageable);
        Page<Promotion> promotionPage = promotionRepository.searchPromotions(searchTerm, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> searchActivePromotions(String searchTerm, Pageable pageable) {
        log.info("Searching active promotions with term: {} and pagination: {}", searchTerm, pageable);
        Page<Promotion> promotionPage = promotionRepository.searchActivePromotions(searchTerm, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> fullTextSearchPromotions(String searchTerm) {
        log.info("Full-text searching promotions with term: {}", searchTerm);
        List<Promotion> promotions = promotionRepository.fullTextSearch(searchTerm);
        return promotions.stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Promotion Application (Main Integration) ====================

    @Override
    public PromotionApplicationResult applyPromotion(ApplyPromotionRequest request) {
        log.info("Applying promotion code: {} for user: {} on order: {}",
                request.getPromotionCode(), request.getUserId(), request.getOrderId());

        try {
            // Check if order already has a promotion applied
            if (promotionUsageRepository.existsByOrderIdAndStatusApplied(request.getOrderId())) {
                return PromotionApplicationResult.alreadyUsed();
            }

            // Find and validate promotion
            Instant now = Instant.now();
            Promotion promotion = promotionRepository.findValidPromotionByCode(request.getPromotionCode(), now)
                    .orElse(null);

            if (promotion == null) {
                // Check if promotion exists but is invalid
                Promotion existingPromotion = promotionRepository.findByCode(request.getPromotionCode())
                        .orElse(null);

                if (existingPromotion == null) {
                    return PromotionApplicationResult.notFound();
                } else if (existingPromotion.isExpired()) {
                    return PromotionApplicationResult.expired();
                } else if (existingPromotion.isNotStarted()) {
                    return PromotionApplicationResult.notStarted();
                } else if (!existingPromotion.hasStock()) {
                    return PromotionApplicationResult.outOfStock();
                } else if (!Boolean.TRUE.equals(existingPromotion.getIsActive())) {
                    return PromotionApplicationResult.inactive();
                }
            }

            // Check minimum order amount
            if (promotion.getMinOrderAmount() != null &&
                request.getOrderAmount().compareTo(promotion.getMinOrderAmount()) < 0) {
                return PromotionApplicationResult.minOrderNotMet(promotion.getMinOrderAmount());
            }

            // Check max uses per user
            if (promotion.getMaxUsesPerUser() != null) {
                long userUsageCount = promotionUsageRepository.countUserPromotionUsage(
                        request.getUserId(), promotion.getId());
                if (userUsageCount >= promotion.getMaxUsesPerUser()) {
                    return PromotionApplicationResult.maxUsesExceeded();
                }
            }

            // Check first time user restriction
            if (Boolean.TRUE.equals(promotion.getFirstTimeUserOnly()) &&
                !Boolean.TRUE.equals(request.getIsFirstTimeUser())) {
                return PromotionApplicationResult.notApplicable();
            }

            // Calculate discount
            BigDecimal discountAmount = promotion.calculateDiscount(request.getOrderAmount());
            if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return PromotionApplicationResult.notApplicable();
            }

            BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);

            // Use promotion (decrease stock, increase usage count)
            int updatedRows = promotionRepository.usePromotion(promotion.getId());
            if (updatedRows == 0) {
                return PromotionApplicationResult.outOfStock();
            }

            // Create usage record
            PromotionUsage usage = PromotionUsage.builder()
                    .promotionId(promotion.getId())
                    .promotionCode(promotion.getCode())
                    .userId(request.getUserId())
                    .orderId(request.getOrderId())
                    .orderAmount(request.getOrderAmount())
                    .discountAmount(discountAmount)
                    .finalAmount(finalAmount)
                    .status("APPLIED")
                    .ipAddress(request.getIpAddress())
                    .userAgent(request.getUserAgent())
                    .notes(request.getNotes())
                    .build();

            PromotionUsage savedUsage = promotionUsageRepository.save(usage);

            log.info("Promotion applied successfully: {} for order: {} with discount: {}",
                    promotion.getCode(), request.getOrderId(), discountAmount);

            return PromotionApplicationResult.success(
                    promotion.getCode(),
                    promotion.getId(),
                    promotion.getName(),
                    promotion.getType(),
                    request.getOrderAmount(),
                    discountAmount,
                    finalAmount,
                    savedUsage.getId()
            );

        } catch (Exception e) {
            log.error("Error applying promotion: {}", e.getMessage(), e);
            return PromotionApplicationResult.failure("Internal error occurred", "INTERNAL_ERROR");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionApplicationResult validatePromotion(String promotionCode, Integer userId, BigDecimal orderAmount) {
        log.info("Validating promotion code: {} for user: {} with amount: {}", promotionCode, userId, orderAmount);

        Instant now = Instant.now();
        Promotion promotion = promotionRepository.findValidPromotionByCode(promotionCode, now)
                .orElse(null);

        if (promotion == null) {
            return PromotionApplicationResult.notFound();
        }

        // Validate all conditions without applying
        if (promotion.getMinOrderAmount() != null &&
            orderAmount.compareTo(promotion.getMinOrderAmount()) < 0) {
            return PromotionApplicationResult.minOrderNotMet(promotion.getMinOrderAmount());
        }

        if (promotion.getMaxUsesPerUser() != null) {
            long userUsageCount = promotionUsageRepository.countUserPromotionUsage(userId, promotion.getId());
            if (userUsageCount >= promotion.getMaxUsesPerUser()) {
                return PromotionApplicationResult.maxUsesExceeded();
            }
        }

        BigDecimal discountAmount = promotion.calculateDiscount(orderAmount);
        BigDecimal finalAmount = orderAmount.subtract(discountAmount);

        return PromotionApplicationResult.success(
                promotion.getCode(),
                promotion.getId(),
                promotion.getName(),
                promotion.getType(),
                orderAmount,
                discountAmount,
                finalAmount,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getAutoApplyPromotions(BigDecimal orderAmount, Integer userId) {
        log.info("Getting auto-apply promotions for user: {} with order amount: {}", userId, orderAmount);
        Instant now = Instant.now();
        List<Promotion> promotions = promotionRepository.findAutoApplyPromotions(now);

        return promotions.stream()
                .filter(promotion -> {
                    // Check minimum order amount
                    if (promotion.getMinOrderAmount() != null &&
                        orderAmount.compareTo(promotion.getMinOrderAmount()) < 0) {
                        return false;
                    }

                    // Check max uses per user
                    if (promotion.getMaxUsesPerUser() != null) {
                        long userUsageCount = promotionUsageRepository.countUserPromotionUsage(userId, promotion.getId());
                        if (userUsageCount >= promotion.getMaxUsesPerUser()) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Promotion Usage Management ====================

    @Override
    public void cancelPromotionUsage(Long usageId) {
        log.info("Cancelling promotion usage ID: {}", usageId);
        PromotionUsage usage = promotionUsageRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("Promotion usage not found with ID: " + usageId));

        if (!"APPLIED".equals(usage.getStatus())) {
            throw new RuntimeException("Can only cancel applied promotion usage");
        }

        usage.cancel();
        promotionUsageRepository.save(usage);

        // Restore promotion stock
        promotionRepository.restorePromotionStock(usage.getPromotionId());

        log.info("Promotion usage cancelled successfully: {}", usageId);
    }

    @Override
    public void refundPromotionUsage(Long usageId) {
        log.info("Refunding promotion usage ID: {}", usageId);
        PromotionUsage usage = promotionUsageRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("Promotion usage not found with ID: " + usageId));

        if (!"APPLIED".equals(usage.getStatus())) {
            throw new RuntimeException("Can only refund applied promotion usage");
        }

        usage.refund();
        promotionUsageRepository.save(usage);

        // Restore promotion stock
        promotionRepository.restorePromotionStock(usage.getPromotionId());

        log.info("Promotion usage refunded successfully: {}", usageId);
    }

    @Override
    public void restorePromotionStock(Long promotionId) {
        log.info("Restoring stock for promotion ID: {}", promotionId);
        promotionRepository.restorePromotionStock(promotionId);
    }

    // ==================== Utility Methods ====================

    @Override
    public boolean isPromotionCodeUnique(String code) {
        return !promotionRepository.findByCode(code.toUpperCase()).isPresent();
    }

    @Override
    public String generatePromotionCode(String prefix) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        if (prefix != null && !prefix.isEmpty()) {
            code.append(prefix.toUpperCase()).append("-");
        }

        for (int i = 0; i < 8; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Ensure uniqueness
        String generatedCode = code.toString();
        while (!isPromotionCodeUnique(generatedCode)) {
            code = new StringBuilder();
            if (prefix != null && !prefix.isEmpty()) {
                code.append(prefix.toUpperCase()).append("-");
            }
            for (int i = 0; i < 8; i++) {
                code.append(characters.charAt(random.nextInt(characters.length())));
            }
            generatedCode = code.toString();
        }

        return generatedCode;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String promotionCode, BigDecimal orderAmount) {
        Instant now = Instant.now();
        Promotion promotion = promotionRepository.findValidPromotionByCode(promotionCode, now)
                .orElse(null);

        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        return promotion.calculateDiscount(orderAmount);
    }

    // ==================== Analytics Methods ====================

    @Override
    @Transactional(readOnly = true)
    public long countActivePromotions() {
        Instant now = Instant.now();
        return promotionRepository.countActivePromotions(now);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFeaturedPromotions() {
        return promotionRepository.countByIsFeaturedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPromotionsByType(String type) {
        return promotionRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOutOfStockPromotions() {
        return promotionRepository.countOutOfStockPromotions();
    }

    @Override
    @Transactional(readOnly = true)
    public long countExpiredPromotions() {
        Instant now = Instant.now();
        return promotionRepository.countExpiredPromotions(now);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalUsageCount() {
        return promotionRepository.getTotalUsageCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageUsageCount() {
        return promotionRepository.getAverageUsageCount();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserPromotionUsage(Integer userId, Long promotionId) {
        return promotionUsageRepository.countUserPromotionUsage(userId, promotionId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDiscountByUser(Integer userId) {
        return promotionUsageRepository.getTotalDiscountByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserTotalUsage(Integer userId) {
        return promotionUsageRepository.countUsageByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPromotionUsage(Long promotionId) {
        return promotionUsageRepository.countUsageByPromotionId(promotionId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDiscountByPromotion(Long promotionId) {
        return promotionUsageRepository.getTotalDiscountByPromotionId(promotionId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageDiscountByPromotion(Long promotionId) {
        return promotionUsageRepository.getAverageDiscountByPromotionId(promotionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUniqueUsersByPromotion(Long promotionId) {
        return promotionUsageRepository.countUniqueUsersByPromotionId(promotionId);
    }

    // ==================== Stub implementations for remaining methods ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getPromotionsByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<Promotion> promotionPage = promotionRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getPromotionsByStartDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<Promotion> promotionPage = promotionRepository.findByStartDateBetween(startDate, endDate, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getPromotionsByEndDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<Promotion> promotionPage = promotionRepository.findByEndDateBetween(startDate, endDate, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PromotionDto> getLowStockPromotions(Integer threshold, Pageable pageable) {
        Page<Promotion> promotionPage = promotionRepository.findLowStockPromotions(threshold, pageable);
        List<PromotionDto> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        return CollectionResponse.of(promotions, promotionPage.getTotalElements(), promotionPage.getTotalPages(),
                promotionPage.getNumber(), promotionPage.getSize());
    }

    @Override
    public void updatePromotionStock(Long promotionId, Integer newStock) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));
        promotion.setStock(newStock);
        promotionRepository.save(promotion);
    }

    @Override
    public PromotionDto activatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotion.setIsActive(true);
        Promotion updated = promotionRepository.save(promotion);
        return promotionMapper.toDTO(updated);
    }

    @Override
    public PromotionDto deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotion.setIsActive(false);
        Promotion updated = promotionRepository.save(promotion);
        return promotionMapper.toDTO(updated);
    }

    @Override
    public PromotionDto featurePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotion.setIsFeatured(true);
        Promotion updated = promotionRepository.save(promotion);
        return promotionMapper.toDTO(updated);
    }

    @Override
    public PromotionDto unfeaturePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotion.setIsFeatured(false);
        Promotion updated = promotionRepository.save(promotion);
        return promotionMapper.toDTO(updated);
    }

    @Override
    public void activateScheduledPromotions() {
        // Implementation for scheduled task
        log.info("Activating scheduled promotions");
    }

    @Override
    public void deactivateExpiredPromotions() {
        Instant now = Instant.now();
        promotionRepository.deactivateExpiredPromotions(now);
    }

    @Override
    public void deactivateOutOfStockPromotions() {
        promotionRepository.deactivateOutOfStockPromotions();
    }

    @Override
    public void cleanupExpiredPromotions() {
        log.info("Cleaning up expired promotions");
    }
}
