package com.pm.orderservice.dto;

import com.pm.orderservice.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDto implements Serializable {
    private Long orderId;
    private String orderNumber;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    private String userPhone;

    // Shipping Address
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;

    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;

    // Billing Address
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    private OrderStatus status;

    // Financial Information
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be positive")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", message = "Tax amount must be positive")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", message = "Shipping fee must be positive")
    private BigDecimal shippingFee;

    @DecimalMin(value = "0.0", message = "Discount amount must be positive")
    private BigDecimal discountAmount;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must be positive")
    private BigDecimal totalAmount;

    // Discount Information
    private String discountCode;
    private String discountType;

    // Payment Information
    private String paymentMethod;
    private String paymentStatus;
    private String paymentId;
    private String transactionId;

    // Shipping Information
    private String shippingMethod;
    private String trackingNumber;
    private Instant estimatedDeliveryDate;
    private Instant actualDeliveryDate;

    // Order Dates
    private Instant orderDate;
    private Instant confirmedDate;
    private Instant shippedDate;
    private Instant deliveredDate;
    private Instant cancelledDate;

    // Additional Information
    private String notes;
    private String adminNotes;
    private String cancellationReason;
    private Boolean isGift;
    private String giftMessage;

    @NotEmpty(message = "Order must contain at least one item")
    private Set<OrderItemDto> orderItems;

    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public int getTotalItems() {
        return orderItems != null ? orderItems.stream()
                .mapToInt(OrderItemDto::getQuantity)
                .sum() : 0;
    }

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
}