package com.pm.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class AddToCartRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "User ID must not be null")
    private Integer userId;
    
    @NotNull(message = "Product ID must not be null")
    private UUID productId;
    
    @NotNull(message = "Product name must not be null")
    private String productName;
    
    @NotNull(message = "Product SKU must not be null")
    private String productSku;
    
    @NotNull(message = "Unit price must not be null")
    @Min(value = 0, message = "Unit price must be greater than or equal to 0")
    private Double unitPrice;
    
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
    
    private String productImageUrl;
} 