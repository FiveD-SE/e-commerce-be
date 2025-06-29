package com.pm.orderservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"orderItems"})
@Data
@SuperBuilder
public class Order extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", unique = true, nullable = false, updatable = false)
    private Long orderId;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @Column(name = "user_phone", length = 20)
    private String userPhone;

    // Shipping Address Details
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_country", nullable = false, length = 100)
    private String shippingCountry;

    // Billing Address (can be same as shipping)
    @Column(name = "billing_address", length = 500)
    private String billingAddress;

    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Column(name = "billing_postal_code", length = 20)
    private String billingPostalCode;

    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    // Financial Information
    @Column(name = "subtotal", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal subtotal;

    @Column(name = "tax_amount", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal taxAmount;

    @Column(name = "shipping_fee", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal shippingFee;

    @Column(name = "discount_amount", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal discountAmount;

    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal totalAmount;

    // Discount Information
    @Column(name = "discount_code", length = 50)
    private String discountCode;

    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    // Payment Information
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // CREDIT_CARD, PAYPAL, BANK_TRANSFER, COD

    @Column(name = "payment_status", length = 20)
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // Shipping Information
    @Column(name = "shipping_method", length = 50)
    private String shippingMethod; // STANDARD, EXPRESS, OVERNIGHT

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "estimated_delivery_date")
    private Instant estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    // Order Dates
    @Column(name = "order_date", nullable = false)
    private Instant orderDate;

    @Column(name = "confirmed_date")
    private Instant confirmedDate;

    @Column(name = "shipped_date")
    private Instant shippedDate;

    @Column(name = "delivered_date")
    private Instant deliveredDate;

    @Column(name = "cancelled_date")
    private Instant cancelledDate;

    // Additional Information
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Builder.Default
    @Column(name = "is_gift")
    private Boolean isGift = false;

    @Column(name = "gift_message", columnDefinition = "TEXT")
    private String giftMessage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = Instant.now();
        }
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    // Helper methods
    public boolean isPaid() {
        return "PAID".equals(paymentStatus);
    }

    public boolean isShipped() {
        return status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean canBeShipped() {
        return status == OrderStatus.CONFIRMED && isPaid();
    }
}