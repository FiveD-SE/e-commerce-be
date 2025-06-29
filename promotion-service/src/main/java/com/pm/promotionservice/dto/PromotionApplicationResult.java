package com.pm.promotionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PromotionApplicationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String promotionCode;
    private Long promotionId;
    private String promotionName;
    private String promotionType;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal discountPercentage;
    private Long usageId;
    private String errorCode;

    // Static factory methods
    public static PromotionApplicationResult success(
            String promotionCode, 
            Long promotionId, 
            String promotionName,
            String promotionType,
            BigDecimal originalAmount, 
            BigDecimal discountAmount, 
            BigDecimal finalAmount,
            Long usageId) {
        
        BigDecimal discountPercentage = BigDecimal.ZERO;
        if (originalAmount != null && originalAmount.compareTo(BigDecimal.ZERO) > 0) {
            discountPercentage = discountAmount.multiply(BigDecimal.valueOf(100))
                    .divide(originalAmount, 2, BigDecimal.ROUND_HALF_UP);
        }

        return PromotionApplicationResult.builder()
                .success(true)
                .message("Promotion applied successfully")
                .promotionCode(promotionCode)
                .promotionId(promotionId)
                .promotionName(promotionName)
                .promotionType(promotionType)
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .discountPercentage(discountPercentage)
                .usageId(usageId)
                .build();
    }

    public static PromotionApplicationResult failure(String message, String errorCode) {
        return PromotionApplicationResult.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static PromotionApplicationResult notFound() {
        return failure("Promotion code not found", "PROMOTION_NOT_FOUND");
    }

    public static PromotionApplicationResult expired() {
        return failure("Promotion code has expired", "PROMOTION_EXPIRED");
    }

    public static PromotionApplicationResult notStarted() {
        return failure("Promotion code is not yet active", "PROMOTION_NOT_STARTED");
    }

    public static PromotionApplicationResult outOfStock() {
        return failure("Promotion code is out of stock", "PROMOTION_OUT_OF_STOCK");
    }

    public static PromotionApplicationResult inactive() {
        return failure("Promotion code is inactive", "PROMOTION_INACTIVE");
    }

    public static PromotionApplicationResult minOrderNotMet(BigDecimal minAmount) {
        return failure("Minimum order amount of $" + minAmount + " not met", "MIN_ORDER_NOT_MET");
    }

    public static PromotionApplicationResult maxUsesExceeded() {
        return failure("Maximum uses per user exceeded", "MAX_USES_EXCEEDED");
    }

    public static PromotionApplicationResult notApplicable() {
        return failure("Promotion is not applicable to this order", "NOT_APPLICABLE");
    }

    public static PromotionApplicationResult alreadyUsed() {
        return failure("Promotion has already been used for this order", "ALREADY_USED");
    }
}
