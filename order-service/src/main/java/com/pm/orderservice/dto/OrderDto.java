package com.pm.orderservice.dto;

import com.pm.orderservice.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDto implements Serializable {
    private Long orderId;
    private Integer userId;
    private String address;
    private OrderStatus status;
    private Double totalAmount;
    private String paymentId;
    private Set<OrderItemDto> orderItems;
    private Instant createdAt;
    private Instant updatedAt;
} 