package com.pm.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDto implements Serializable {
    private Long orderItemId;
    private UUID productId;
    private String productName;
    private String productSku;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;
    private String productImageUrl;
} 