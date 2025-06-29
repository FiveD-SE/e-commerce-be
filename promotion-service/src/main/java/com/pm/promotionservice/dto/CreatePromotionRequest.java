package com.pm.promotionservice.dto;

import jakarta.validation.constraints.*;
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
public class CreatePromotionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Promotion name is required")
    @Size(max = 255, message = "Promotion name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Promotion code is required")
    @Size(max = 50, message = "Promotion code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Promotion code must contain only uppercase letters, numbers, underscores, and hyphens")
    private String code;

    private String description;

    @NotBlank(message = "Promotion type is required")
    @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT|FREE_SHIPPING)$", message = "Type must be PERCENTAGE, FIXED_AMOUNT, or FREE_SHIPPING")
    private String type;

    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    private BigDecimal percent;

    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.01", message = "Max discount must be greater than 0")
    private BigDecimal maxDiscount;

    @DecimalMin(value = "0.01", message = "Minimum order amount must be greater than 0")
    private BigDecimal minOrderAmount;

    @NotNull(message = "Stock is required")
    @Min(value = 1, message = "Stock must be at least 1")
    private Integer stock;

    @Min(value = 1, message = "Max uses per user must be at least 1")
    private Integer maxUsesPerUser;

    @NotNull(message = "Start date is required")
    private Instant startDate;

    @NotNull(message = "End date is required")
    private Instant endDate;

    private Boolean isActive;

    private Boolean isFeatured;

    private Boolean isStackable;

    @Size(max = 500, message = "Applicable categories must not exceed 500 characters")
    private String applicableCategories;

    @Size(max = 500, message = "Applicable products must not exceed 500 characters")
    private String applicableProducts;

    @Size(max = 500, message = "Applicable brands must not exceed 500 characters")
    private String applicableBrands;

    @Size(max = 500, message = "Excluded categories must not exceed 500 characters")
    private String excludedCategories;

    @Size(max = 500, message = "Excluded products must not exceed 500 characters")
    private String excludedProducts;

    @Size(max = 500, message = "User groups must not exceed 500 characters")
    private String userGroups;

    private Boolean firstTimeUserOnly;

    private Boolean autoApply;

    private Integer priority;

    private Integer createdBy;

    // Custom validation
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Percentage discount requires percent value")
    public boolean isPercentageValid() {
        if ("PERCENTAGE".equals(type)) {
            return percent != null && percent.compareTo(BigDecimal.ZERO) > 0;
        }
        return true;
    }

    @AssertTrue(message = "Fixed amount discount requires discount amount value")
    public boolean isFixedAmountValid() {
        if ("FIXED_AMOUNT".equals(type)) {
            return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
        }
        return true;
    }

    @AssertTrue(message = "Start date must be in the future or present")
    public boolean isStartDateValid() {
        if (startDate == null) {
            return true;
        }
        return !startDate.isBefore(Instant.now().minusSeconds(60)); // Allow 1 minute tolerance
    }
}
