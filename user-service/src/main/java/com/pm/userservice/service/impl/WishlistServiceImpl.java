package com.pm.userservice.service.impl;

import com.pm.userservice.dto.WishlistDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.UserObjectNotFoundException;
import com.pm.userservice.mapper.WishlistMapperUtils;
import com.pm.userservice.model.User;
import com.pm.userservice.model.Wishlist;
import com.pm.userservice.repository.UserRepository;
import com.pm.userservice.repository.WishlistRepository;
import com.pm.userservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<WishlistDto> findByUserId(Integer userId) {
        log.info("Finding wishlist for user ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException("User not found with ID: " + userId);
        }
        
        List<Wishlist> wishlists = wishlistRepository.findByUserUserId(userId);
        List<WishlistDto> wishlistDtos = wishlists.stream()
                .map(WishlistMapperUtils::map)
                .collect(Collectors.toList());
        
        return CollectionResponse.<WishlistDto>builder()
                .data(wishlistDtos)
                .totalElements(wishlistDtos.size())
                .build();
    }

    @Override
    public WishlistDto addToWishlist(Integer userId, Integer productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException("User not found with ID: " + userId));
        
        // Check if product is already in wishlist
        if (wishlistRepository.existsByUserUserIdAndProductId(userId, productId)) {
            log.warn("Product {} is already in wishlist for user {}", productId, userId);
            return WishlistMapperUtils.map(wishlistRepository.findByUserUserIdAndProductId(userId, productId).get());
        }
        
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .productId(productId)
                .build();
        
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("Successfully added product {} to wishlist for user {}", productId, userId);
        
        return WishlistMapperUtils.map(savedWishlist);
    }

    @Override
    public void removeFromWishlist(Integer userId, Integer productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        
        if (!userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException("User not found with ID: " + userId);
        }
        
        wishlistRepository.deleteByUserUserIdAndProductId(userId, productId);
        log.info("Successfully removed product {} from wishlist for user {}", productId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Integer userId, Integer productId) {
        return wishlistRepository.existsByUserUserIdAndProductId(userId, productId);
    }

    @Override
    public void clearWishlist(Integer userId) {
        log.info("Clearing wishlist for user {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException("User not found with ID: " + userId);
        }
        
        List<Wishlist> wishlists = wishlistRepository.findByUserUserId(userId);
        wishlistRepository.deleteAll(wishlists);
        log.info("Successfully cleared wishlist for user {}", userId);
    }

} 