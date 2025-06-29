package com.pm.userservice.repository;

import com.pm.userservice.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    List<Wishlist> findByUserUserId(Integer userId);

    Optional<Wishlist> findByUserUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserUserIdAndProductId(Integer userId, Integer productId);

    @Query("SELECT w FROM Wishlist w WHERE w.user.userId = :userId")
    List<Wishlist> findWishlistsByUserId(@Param("userId") Integer userId);

    boolean existsByUserUserIdAndProductId(Integer userId, Integer productId);

} 