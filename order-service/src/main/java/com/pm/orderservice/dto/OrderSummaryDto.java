package com.pm.orderservice.dto;

import com.pm.orderservice.model.OrderStatus;
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
public class OrderSummaryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNumber;
    private Integer userId;
    private String userEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String shippingMethod;
    private String trackingNumber;
    private Instant orderDate;
    private Instant estimatedDeliveryDate;
    private int totalItems;
    
    // Computed fields
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
