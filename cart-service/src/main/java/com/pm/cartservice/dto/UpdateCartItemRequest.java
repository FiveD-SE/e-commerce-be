package com.pm.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateCartItemRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "Cart item ID must not be null")
    private Integer cartItemId;
    
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
} 