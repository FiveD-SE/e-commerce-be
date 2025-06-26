package com.pm.cartservice.service.impl;

import com.pm.cartservice.dto.CartDto;
import com.pm.cartservice.dto.CartItemDto;
import com.pm.cartservice.dto.AddToCartRequest;
import com.pm.cartservice.dto.response.collection.CollectionResponse;
import com.pm.cartservice.mapper.CartMapper;
import com.pm.cartservice.mapper.CartItemMapper;
import com.pm.cartservice.model.Cart;
import com.pm.cartservice.model.CartItem;
import com.pm.cartservice.model.CartStatus;
import com.pm.cartservice.repository.CartRepository;
import com.pm.cartservice.repository.CartItemRepository;
import com.pm.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional(readOnly = true)
    public CartDto findCartByUserId(Integer userId) {
        log.info("Finding cart for user ID: {}", userId);
        Optional<Cart> cart = cartRepository.findActiveCartByUserId(userId);
        return cart.map(cartMapper::toDTO).orElse(null);
    }

    @Override
    public CartDto addToCart(AddToCartRequest request) {
        log.info("Adding item to cart for user ID: {}", request.getUserId());
        
        // Find or create active cart for user
        Cart cart = cartRepository.findActiveCartByUserId(request.getUserId())
                .orElseGet(() -> createNewCart(request.getUserId()));
        
        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findCartItemByCartIdAndProductId(
                cart.getCartId(), request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setTotalPrice(item.getUnitPrice() * item.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .productSku(request.getProductSku())
                    .unitPrice(request.getUnitPrice())
                    .quantity(request.getQuantity())
                    .totalPrice(request.getUnitPrice() * request.getQuantity())
                    .productImageUrl(request.getProductImageUrl())
                    .build();
            cartItemRepository.save(newItem);
        }
        
        // Update cart totals
        updateCartTotals(cart);
        Cart savedCart = cartRepository.save(cart);
        
        return cartMapper.toDTO(savedCart);
    }

    @Override
    public CartDto updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        log.info("Updating cart item quantity: {} to {}", cartItemId, quantity);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice() * quantity);
        cartItemRepository.save(cartItem);
        
        // Update cart totals
        Cart cart = cartItem.getCart();
        updateCartTotals(cart);
        Cart savedCart = cartRepository.save(cart);
        
        return cartMapper.toDTO(savedCart);
    }

    @Override
    public void removeFromCart(Integer cartId, Integer cartItemId) {
        log.info("Removing cart item: {} from cart: {}", cartItemId, cartId);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getCart().getCartId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to the specified cart");
        }
        
        cartItemRepository.delete(cartItem);
        
        // Update cart totals
        Cart cart = cartItem.getCart();
        updateCartTotals(cart);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Integer cartId) {
        log.info("Clearing cart: {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cartItemRepository.deleteByCartCartId(cartId);
        
        cart.setTotalAmount(0.0);
        cart.setItemCount(0);
        cartRepository.save(cart);
    }

    @Override
    public void convertCartToOrder(Integer cartId) {
        log.info("Converting cart to order: {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.setStatus(CartStatus.CONVERTED_TO_ORDER);
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<CartDto> findAllCarts() {
        log.info("Fetching all carts");
        List<CartDto> carts = cartRepository.findAll().stream()
                .map(cartMapper::toDTO)
                .toList();
        return CollectionResponse.<CartDto>builder()
                .data(carts)
                .totalElements(carts.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto findCartById(Integer cartId) {
        log.info("Finding cart by ID: {}", cartId);
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartMapper.toDTO(cart);
    }

    private Cart createNewCart(Integer userId) {
        Cart newCart = Cart.builder()
                .userId(userId)
                .status(CartStatus.ACTIVE)
                .totalAmount(0.0)
                .itemCount(0)
                .build();
        return cartRepository.save(newCart);
    }

    private void updateCartTotals(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartCartId(cart.getCartId());
        
        double totalAmount = items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        
        int itemCount = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        cart.setTotalAmount(totalAmount);
        cart.setItemCount(itemCount);
    }
} 