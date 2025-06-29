package com.pm.promotionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false, length = 20)
    @Builder.Default
    private String type = "PERCENTAGE"; // PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal percent;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "max_uses_per_user")
    private Integer maxUsesPerUser;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_stackable", nullable = false)
    @Builder.Default
    private Boolean isStackable = false;

    @Column(name = "applicable_categories", length = 500)
    private String applicableCategories; // JSON array of category IDs

    @Column(name = "applicable_products", length = 500)
    private String applicableProducts; // JSON array of product IDs

    @Column(name = "applicable_brands", length = 500)
    private String applicableBrands; // JSON array of brand IDs

    @Column(name = "excluded_categories", length = 500)
    private String excludedCategories; // JSON array of category IDs

    @Column(name = "excluded_products", length = 500)
    private String excludedProducts; // JSON array of product IDs

    @Column(name = "user_groups", length = 500)
    private String userGroups; // JSON array of user group IDs

    @Column(name = "first_time_user_only", nullable = false)
    @Builder.Default
    private Boolean firstTimeUserOnly = false;

    @Column(name = "auto_apply", nullable = false)
    @Builder.Default
    private Boolean autoApply = false;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Helper methods
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

    public boolean hasStock() {
        return this.stock > 0;
    }

    public void usePromotion() {
        if (this.stock > 0) {
            this.stock--;
            this.usedCount++;
        }
    }

    public void restoreStock() {
        this.stock++;
        if (this.usedCount > 0) {
            this.usedCount--;
        }
    }

    public boolean isPercentageDiscount() {
        return "PERCENTAGE".equals(this.type);
    }

    public boolean isFixedAmountDiscount() {
        return "FIXED_AMOUNT".equals(this.type);
    }

    public boolean isFreeShipping() {
        return "FREE_SHIPPING".equals(this.type);
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid() || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Check minimum order amount
        if (this.minOrderAmount != null && orderAmount.compareTo(this.minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (isPercentageDiscount() && this.percent != null) {
            discount = orderAmount.multiply(this.percent).divide(BigDecimal.valueOf(100));
            
            // Apply max discount limit
            if (this.maxDiscount != null && discount.compareTo(this.maxDiscount) > 0) {
                discount = this.maxDiscount;
            }
        } else if (isFixedAmountDiscount() && this.discountAmount != null) {
            discount = this.discountAmount;
            
            // Discount cannot exceed order amount
            if (discount.compareTo(orderAmount) > 0) {
                discount = orderAmount;
            }
        }

        return discount;
    }

    public BigDecimal getUsagePercentage() {
        if (this.stock + this.usedCount == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(this.usedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(this.stock + this.usedCount), 2, BigDecimal.ROUND_HALF_UP);
    }
}
