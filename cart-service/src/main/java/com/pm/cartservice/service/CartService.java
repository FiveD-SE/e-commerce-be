package com.pm.cartservice.service;

import com.pm.cartservice.dto.CartDto;
import com.pm.cartservice.dto.AddToCartRequest;
import com.pm.cartservice.dto.response.collection.CollectionResponse;

public interface CartService {
    CartDto findCartByUserId(Integer userId);
    CartDto addToCart(AddToCartRequest request);
    CartDto updateCartItemQuantity(Integer cartItemId, Integer quantity);
    void removeFromCart(Integer cartId, Integer cartItemId);
    void clearCart(Integer cartId);
    void convertCartToOrder(Integer cartId);
    CollectionResponse<CartDto> findAllCarts();
    CartDto findCartById(Integer cartId);
} 