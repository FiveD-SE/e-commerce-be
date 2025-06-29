package com.pm.promotionservice.repository;

import com.pm.promotionservice.model.PromotionUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {

    // Basic queries
    List<PromotionUsage> findByPromotionId(Long promotionId);
    Page<PromotionUsage> findByPromotionId(Long promotionId, Pageable pageable);

    List<PromotionUsage> findByPromotionCode(String promotionCode);
    Page<PromotionUsage> findByPromotionCode(String promotionCode, Pageable pageable);

    List<PromotionUsage> findByUserId(Integer userId);
    Page<PromotionUsage> findByUserId(Integer userId, Pageable pageable);

    Optional<PromotionUsage> findByOrderId(Long orderId);
    List<PromotionUsage> findByPaymentId(Long paymentId);

    // Status queries
    List<PromotionUsage> findByStatus(String status);
    Page<PromotionUsage> findByStatus(String status, Pageable pageable);

    // User and promotion combination
    List<PromotionUsage> findByUserIdAndPromotionId(Integer userId, Long promotionId);
    Page<PromotionUsage> findByUserIdAndPromotionId(Integer userId, Long promotionId, Pageable pageable);

    List<PromotionUsage> findByUserIdAndPromotionCode(Integer userId, String promotionCode);
    Page<PromotionUsage> findByUserIdAndPromotionCode(Integer userId, String promotionCode, Pageable pageable);

    // Count user usage
    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.userId = :userId AND pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    long countUserPromotionUsage(@Param("userId") Integer userId, @Param("promotionId") Long promotionId);

    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.userId = :userId AND pu.promotionCode = :promotionCode AND pu.status = 'APPLIED'")
    long countUserPromotionUsageByCode(@Param("userId") Integer userId, @Param("promotionCode") String promotionCode);

    // Check if order already has promotion
    @Query("SELECT COUNT(pu) > 0 FROM PromotionUsage pu WHERE pu.orderId = :orderId AND pu.status = 'APPLIED'")
    boolean existsByOrderIdAndStatusApplied(@Param("orderId") Long orderId);

    // Date range queries
    List<PromotionUsage> findByAppliedAtBetween(Instant startDate, Instant endDate);
    Page<PromotionUsage> findByAppliedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    // Analytics queries
    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    long countUsageByPromotionId(@Param("promotionId") Long promotionId);

    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.promotionCode = :promotionCode AND pu.status = 'APPLIED'")
    long countUsageByPromotionCode(@Param("promotionCode") String promotionCode);

    @Query("SELECT SUM(pu.discountAmount) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    BigDecimal getTotalDiscountByPromotionId(@Param("promotionId") Long promotionId);

    @Query("SELECT SUM(pu.discountAmount) FROM PromotionUsage pu WHERE pu.promotionCode = :promotionCode AND pu.status = 'APPLIED'")
    BigDecimal getTotalDiscountByPromotionCode(@Param("promotionCode") String promotionCode);

    @Query("SELECT AVG(pu.discountAmount) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    BigDecimal getAverageDiscountByPromotionId(@Param("promotionId") Long promotionId);

    // User analytics
    @Query("SELECT COUNT(DISTINCT pu.userId) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    long countUniqueUsersByPromotionId(@Param("promotionId") Long promotionId);

    @Query("SELECT SUM(pu.discountAmount) FROM PromotionUsage pu WHERE pu.userId = :userId AND pu.status = 'APPLIED'")
    BigDecimal getTotalDiscountByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.userId = :userId AND pu.status = 'APPLIED'")
    long countUsageByUserId(@Param("userId") Integer userId);

    // Time-based analytics
    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.appliedAt >= :startDate AND pu.appliedAt <= :endDate AND pu.status = 'APPLIED'")
    long countUsageInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT SUM(pu.discountAmount) FROM PromotionUsage pu WHERE pu.appliedAt >= :startDate AND pu.appliedAt <= :endDate AND pu.status = 'APPLIED'")
    BigDecimal getTotalDiscountInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    // Top promotions
    @Query("SELECT pu.promotionCode, COUNT(pu) as usageCount FROM PromotionUsage pu WHERE pu.status = 'APPLIED' GROUP BY pu.promotionCode ORDER BY usageCount DESC")
    List<Object[]> findTopPromotionsByUsage(Pageable pageable);

    @Query("SELECT pu.promotionCode, SUM(pu.discountAmount) as totalDiscount FROM PromotionUsage pu WHERE pu.status = 'APPLIED' GROUP BY pu.promotionCode ORDER BY totalDiscount DESC")
    List<Object[]> findTopPromotionsByDiscount(Pageable pageable);

    // Recent usage
    @Query("SELECT pu FROM PromotionUsage pu WHERE pu.status = 'APPLIED' ORDER BY pu.appliedAt DESC")
    List<PromotionUsage> findRecentUsage(Pageable pageable);

    @Query("SELECT pu FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED' ORDER BY pu.appliedAt DESC")
    List<PromotionUsage> findRecentUsageByPromotionId(@Param("promotionId") Long promotionId, Pageable pageable);

    // Status specific queries
    List<PromotionUsage> findByStatusAndAppliedAtBetween(String status, Instant startDate, Instant endDate);
    Page<PromotionUsage> findByStatusAndAppliedAtBetween(String status, Instant startDate, Instant endDate, Pageable pageable);

    // Cancelled and refunded
    List<PromotionUsage> findByCancelledAtBetween(Instant startDate, Instant endDate);
    List<PromotionUsage> findByRefundedAtBetween(Instant startDate, Instant endDate);

    // Count by status
    long countByStatus(String status);
    long countByPromotionIdAndStatus(Long promotionId, String status);
    long countByUserIdAndStatus(Integer userId, String status);

    // IP and User Agent tracking
    List<PromotionUsage> findByIpAddress(String ipAddress);
    Page<PromotionUsage> findByIpAddress(String ipAddress, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT pu.ipAddress) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.status = 'APPLIED'")
    long countUniqueIpsByPromotionId(@Param("promotionId") Long promotionId);
}
