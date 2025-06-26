package com.pm.cartservice.repository;

import com.pm.cartservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
    List<CartItem> findByCartCartId(Integer cartId);
    
    Optional<CartItem> findByCartCartIdAndProductId(Integer cartId, UUID productId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    Optional<CartItem> findCartItemByCartIdAndProductId(@Param("cartId") Integer cartId, @Param("productId") UUID productId);
    
    void deleteByCartCartIdAndProductId(Integer cartId, UUID productId);
    
    void deleteByCartCartId(Integer cartId);
    
    boolean existsByCartCartIdAndProductId(Integer cartId, UUID productId);
} 