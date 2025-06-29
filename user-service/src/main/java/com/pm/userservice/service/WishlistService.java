package com.pm.userservice.service;

import com.pm.userservice.dto.WishlistDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface WishlistService {

    CollectionResponse<WishlistDto> findByUserId(Integer userId);
    
    WishlistDto addToWishlist(Integer userId, Integer productId);
    
    void removeFromWishlist(Integer userId, Integer productId);
    
    boolean isProductInWishlist(Integer userId, Integer productId);
    
    void clearWishlist(Integer userId);

} 