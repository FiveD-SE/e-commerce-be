package com.pm.cartservice.repository;

import com.pm.cartservice.model.Cart;
import com.pm.cartservice.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    Optional<Cart> findByUserIdAndStatus(Integer userId, CartStatus status);
    
    List<Cart> findByUserId(Integer userId);
    
    List<Cart> findByStatus(CartStatus status);
    
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Integer userId);
    
    boolean existsByUserIdAndStatus(Integer userId, CartStatus status);
} 