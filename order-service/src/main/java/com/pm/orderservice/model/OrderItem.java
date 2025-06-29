package com.pm.orderservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"order"})
@Data
@SuperBuilder
public class OrderItem extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", unique = true, nullable = false, updatable = false)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_sku", nullable = false, length = 50)
    private String productSku;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_category", length = 100)
    private String productCategory;

    @Column(name = "product_brand", length = 100)
    private String productBrand;

    @Column(name = "unit_price", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal totalPrice;

    @Column(name = "discount_amount", columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal discountAmount;

    @Column(name = "tax_amount", columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal taxAmount;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "product_weight", columnDefinition = "DECIMAL(8,2)")
    private BigDecimal productWeight; // in kg

    @Column(name = "product_dimensions", length = 100)
    private String productDimensions; // e.g., "10x20x5 cm"

    @Column(name = "is_digital_product")
    @Builder.Default
    private Boolean isDigitalProduct = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    @PreUpdate
    protected void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
            BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
            this.totalPrice = subtotal.subtract(discount).add(tax);
        }
    }

    // Helper methods
    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getFinalPrice() {
        BigDecimal subtotal = getSubtotal();
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        return subtotal.subtract(discount).add(tax);
    }
}