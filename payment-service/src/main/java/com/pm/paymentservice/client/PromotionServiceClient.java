package com.pm.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "promotion-service", path = "/promotion-service/api/promotions")
public interface PromotionServiceClient {

    @PostMapping("/apply")
    PromotionApplicationResult applyPromotion(@RequestBody ApplyPromotionRequest request);

    @PostMapping("/validate")
    PromotionApplicationResult validatePromotion(
            @RequestParam("code") String code,
            @RequestParam("userId") Integer userId,
            @RequestParam("orderAmount") BigDecimal orderAmount
    );

    @GetMapping("/auto-apply")
    List<PromotionDto> getAutoApplyPromotions(
            @RequestParam("orderAmount") BigDecimal orderAmount,
            @RequestParam("userId") Integer userId
    );

    @PostMapping("/usage/{usageId}/cancel")
    void cancelPromotionUsage(@PathVariable("usageId") Long usageId);

    @PostMapping("/usage/{usageId}/refund")
    void refundPromotionUsage(@PathVariable("usageId") Long usageId);

    // DTOs for Promotion Service Integration
    record ApplyPromotionRequest(
            String promotionCode,
            Integer userId,
            Long orderId,
            BigDecimal orderAmount,
            List<Long> productIds,
            List<Long> categoryIds,
            List<Long> brandIds,
            String userGroup,
            Boolean isFirstTimeUser,
            String ipAddress,
            String userAgent,
            String notes
    ) {}

    record PromotionApplicationResult(
            boolean success,
            String message,
            String promotionCode,
            Long promotionId,
            String promotionName,
            String promotionType,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            BigDecimal discountPercentage,
            Long usageId,
            String errorCode
    ) {}

    record PromotionDto(
            Long id,
            String name,
            String code,
            String description,
            String type,
            BigDecimal percent,
            BigDecimal discountAmount,
            BigDecimal maxDiscount,
            BigDecimal minOrderAmount,
            Integer stock,
            Integer usedCount,
            Integer maxUsesPerUser,
            String startDate,
            String endDate,
            Boolean isActive,
            Boolean isFeatured,
            Boolean isStackable,
            Boolean firstTimeUserOnly,
            Boolean autoApply,
            Integer priority
    ) {}
}
