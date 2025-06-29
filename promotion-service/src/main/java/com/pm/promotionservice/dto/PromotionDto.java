package com.pm.promotionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PromotionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String code;
    private String description;
    private String type;
    private BigDecimal percent;
    private BigDecimal discountAmount;
    private BigDecimal maxDiscount;
    private BigDecimal minOrderAmount;
    private Integer stock;
    private Integer usedCount;
    private Integer maxUsesPerUser;
    private Instant startDate;
    private Instant endDate;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isStackable;
    private String applicableCategories;
    private String applicableProducts;
    private String applicableBrands;
    private String excludedCategories;
    private String excludedProducts;
    private String userGroups;
    private Boolean firstTimeUserOnly;
    private Boolean autoApply;
    private Integer priority;
    private Integer createdBy;
    private Integer updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isValid() {
        Instant now = Instant.now();
        return Boolean.TRUE.equals(this.isActive) 
            && this.startDate != null && !now.isBefore(this.startDate)
            && this.endDate != null && !now.isAfter(this.endDate)
            && this.stock > 0;
    }

    public boolean isExpired() {
        return this.endDate != null && Instant.now().isAfter(this.endDate);
    }

    public boolean isNotStarted() {
        return this.startDate != null && Instant.now().isBefore(this.startDate);
    }

    public String getStatusDisplay() {
        if (isExpired()) {
            return "Expired";
        } else if (isNotStarted()) {
            return "Scheduled";
        } else if (!Boolean.TRUE.equals(isActive)) {
            return "Inactive";
        } else if (stock <= 0) {
            return "Out of Stock";
        } else {
            return "Active";
        }
    }

    public String getTypeDisplay() {
        switch (type != null ? type : "PERCENTAGE") {
            case "PERCENTAGE":
                return "Percentage Discount";
            case "FIXED_AMOUNT":
                return "Fixed Amount Discount";
            case "FREE_SHIPPING":
                return "Free Shipping";
            default:
                return type;
        }
    }

    public BigDecimal getUsagePercentage() {
        if (this.stock + this.usedCount == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(this.usedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(this.stock + this.usedCount), 2, BigDecimal.ROUND_HALF_UP);
    }

    public String getDiscountDisplay() {
        if ("PERCENTAGE".equals(type) && percent != null) {
            return percent + "%";
        } else if ("FIXED_AMOUNT".equals(type) && discountAmount != null) {
            return "$" + discountAmount;
        } else if ("FREE_SHIPPING".equals(type)) {
            return "Free Shipping";
        }
        return "N/A";
    }
}
