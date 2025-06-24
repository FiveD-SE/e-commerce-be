package com.pm.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pm.cartservice.model.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer cartId;
    
    private Integer userId;
    
    private CartStatus status;
    
    private Double totalAmount;
    
    private Integer itemCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<CartItemDto> cartItems;
} 