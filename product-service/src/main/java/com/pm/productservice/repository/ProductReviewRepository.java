package com.pm.productservice.repository;

import com.pm.productservice.model.ProductReview;
import com.pm.productservice.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    
    List<ProductReview> findByProductIdAndStatus(UUID productId, ReviewStatus status);
    
    Page<ProductReview> findByProductIdAndStatus(UUID productId, ReviewStatus status, Pageable pageable);
    
    List<ProductReview> findByUserIdAndStatus(UUID userId, ReviewStatus status);
    
    Page<ProductReview> findByUserIdAndStatus(UUID userId, ReviewStatus status, Pageable pageable);
    
    Optional<ProductReview> findByProductIdAndUserId(UUID productId, UUID userId);
    
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.productId = :productId AND r.status = :status")
    Double findAverageRatingByProductId(@Param("productId") UUID productId, @Param("status") ReviewStatus status);
    
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.productId = :productId AND r.status = :status")
    Long countByProductIdAndStatus(@Param("productId") UUID productId, @Param("status") ReviewStatus status);
    
    @Query("SELECT r FROM ProductReview r WHERE " +
           "(:productId IS NULL OR r.productId = :productId) AND " +
           "(:userId IS NULL OR r.userId = :userId) AND " +
           "(:rating IS NULL OR r.rating = :rating) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<ProductReview> findWithFilters(@Param("productId") UUID productId,
                                      @Param("userId") UUID userId,
                                      @Param("rating") Integer rating,
                                      @Param("status") ReviewStatus status,
                                      Pageable pageable);
} 