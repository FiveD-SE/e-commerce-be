package com.pm.userservice.mapper;

import com.pm.userservice.dto.WishlistDto;
import com.pm.userservice.model.Wishlist;
import com.pm.userservice.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public final class WishlistMapperUtils {

    private WishlistMapperUtils() {
        // Utility class
    }

    public static WishlistDto map(final Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }
        return WishlistDto.builder()
                .wishlistId(wishlist.getWishlistId())
                .productId(wishlist.getProductId())
                .userId(wishlist.getUser() != null ? wishlist.getUser().getUserId() : null)
                .createdAt(wishlist.getCreatedAt())
                .build();
    }

    public static Wishlist map(final WishlistDto wishlistDto) {
        if (wishlistDto == null) {
            return null;
        }
        return Wishlist.builder()
                .wishlistId(wishlistDto.getWishlistId())
                .productId(wishlistDto.getProductId())
                .user(wishlistDto.getUserId() != null ? User.builder().userId(wishlistDto.getUserId()).build() : null)
                .build();
    }

    public static Set<WishlistDto> map(final Set<Wishlist> wishlists) {
        if (wishlists == null) {
            return null;
        }
        return wishlists.stream()
                .map(WishlistMapperUtils::map)
                .collect(Collectors.toSet());
    }

} 