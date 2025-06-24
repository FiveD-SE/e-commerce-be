package com.pm.cartservice.controller;

import com.pm.cartservice.dto.CartDto;
import com.pm.cartservice.dto.AddToCartRequest;
import com.pm.cartservice.dto.UpdateCartItemRequest;
import com.pm.cartservice.dto.response.collection.CollectionResponse;
import com.pm.cartservice.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart Management", description = "APIs for managing shopping carts and cart items")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get all carts")
    public ResponseEntity<CollectionResponse<CartDto>> findAllCarts() {
        log.info("Fetching all carts");
        return ResponseEntity.ok(cartService.findAllCarts());
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get a cart by ID")
    public ResponseEntity<CartDto> findCartById(
            @PathVariable @NotNull(message = "Cart ID must not be null") @Valid Integer cartId) {
        log.info("Fetching cart with ID: {}", cartId);
        return ResponseEntity.ok(cartService.findCartById(cartId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cart by user ID")
    public ResponseEntity<CartDto> findCartByUserId(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching cart for user ID: {}", userId);
        CartDto cart = cartService.findCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartDto> addToCart(
            @RequestBody @NotNull(message = "Request must not be null") @Valid AddToCartRequest request) {
        log.info("Adding item to cart for user ID: {}", request.getUserId());
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @PutMapping("/items/{cartItemId}/quantity")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @PathVariable @NotNull(message = "Cart item ID must not be null") @Valid Integer cartItemId,
            @RequestParam @NotNull(message = "Quantity must not be null") @Min(value = 1, message = "Quantity must be greater than 0") @Valid Integer quantity) {
        log.info("Updating cart item quantity: {} to {}", cartItemId, quantity);
        return ResponseEntity.ok(cartService.updateCartItemQuantity(cartItemId, quantity));
    }

    @PutMapping("/items/quantity")
    @Operation(summary = "Update cart item quantity using request body")
    public ResponseEntity<CartDto> updateCartItemQuantityWithBody(
            @RequestBody @NotNull(message = "Request must not be null") @Valid UpdateCartItemRequest request) {
        log.info("Updating cart item quantity: {} to {}", request.getCartItemId(), request.getQuantity());
        return ResponseEntity.ok(cartService.updateCartItemQuantity(request.getCartItemId(), request.getQuantity()));
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable @NotNull(message = "Cart ID must not be null") @Valid Integer cartId,
            @PathVariable @NotNull(message = "Cart item ID must not be null") @Valid Integer cartItemId) {
        log.info("Removing cart item: {} from cart: {}", cartItemId, cartId);
        cartService.removeFromCart(cartId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/clear")
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<Void> clearCart(
            @PathVariable @NotNull(message = "Cart ID must not be null") @Valid Integer cartId) {
        log.info("Clearing cart: {}", cartId);
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cartId}/convert-to-order")
    @Operation(summary = "Convert cart to order")
    public ResponseEntity<Void> convertCartToOrder(
            @PathVariable @NotNull(message = "Cart ID must not be null") @Valid Integer cartId) {
        log.info("Converting cart to order: {}", cartId);
        cartService.convertCartToOrder(cartId);
        return ResponseEntity.ok().build();
    }
} 