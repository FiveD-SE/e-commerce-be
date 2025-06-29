package com.pm.userservice.controller;

import com.pm.userservice.dto.WishlistDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users/{userId}/wishlist")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<CollectionResponse<WishlistDto>> getUserWishlist(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching wishlist for user ID: {}", userId);
        return ResponseEntity.ok(wishlistService.findByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<WishlistDto> addToWishlist(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestParam @NotNull(message = "Product ID must not be null") @Valid Integer productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        WishlistDto wishlistItem = wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.created(URI.create("/api/users/" + userId + "/wishlist/" + wishlistItem.getWishlistId())).body(wishlistItem);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid Integer productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product is in wishlist")
    public ResponseEntity<Boolean> isProductInWishlist(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @PathVariable @NotNull(message = "Product ID must not be null") @Valid Integer productId) {
        log.info("Checking if product {} is in wishlist for user {}", productId, userId);
        boolean isInWishlist = wishlistService.isProductInWishlist(userId, productId);
        return ResponseEntity.ok(isInWishlist);
    }

    @DeleteMapping
    @Operation(summary = "Clear user wishlist")
    public ResponseEntity<Void> clearWishlist(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Clearing wishlist for user {}", userId);
        wishlistService.clearWishlist(userId);
        return ResponseEntity.noContent().build();
    }

} 