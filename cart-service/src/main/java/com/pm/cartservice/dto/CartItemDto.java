package com.pm.cartservice.dto;

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
public class CartItemDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer cartItemId;
    
    private Integer cartId;
    
    private UUID productId;
    
    private String productName;
    
    private String productSku;
    
    private Double unitPrice;
    
    private Integer quantity;
    
    private Double totalPrice;
    
    private String productImageUrl;
} 