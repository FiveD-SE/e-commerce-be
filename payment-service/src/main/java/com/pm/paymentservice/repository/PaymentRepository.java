package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.Payment;
import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentStatus;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Basic finders
    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    // Order-based queries
    List<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
    
    // User-based queries
    Page<Payment> findByUserId(Integer userId, Pageable pageable);
    List<Payment> findByUserId(Integer userId);
    Page<Payment> findByUserIdAndStatus(Integer userId, PaymentStatus status, Pageable pageable);
    
    // Status-based queries
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    List<Payment> findByStatus(PaymentStatus status);
    long countByStatus(PaymentStatus status);
    long countByUserId(Integer userId);
    
    // Gateway-based queries
    Page<Payment> findByGateway(PaymentGateway gateway, Pageable pageable);
    List<Payment> findByGateway(PaymentGateway gateway);
    
    // Date-based queries
    Page<Payment> findByInitiatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<Payment> findByInitiatedAtBetween(Instant startDate, Instant endDate);
    
    Page<Payment> findByCompletedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<Payment> findByCompletedAtBetween(Instant startDate, Instant endDate);
    
    // Expired payments
    List<Payment> findByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiredBefore);
    
    // Amount-based queries
    Page<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    // Search queries
    @Query("SELECT p FROM Payment p WHERE " +
           "p.paymentReference LIKE %:searchTerm% OR " +
           "p.userEmail LIKE %:searchTerm% OR " +
           "p.gatewayTransactionId LIKE %:searchTerm% OR " +
           "p.description LIKE %:searchTerm%")
    Page<Payment> searchPayments(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Complex queries
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.status IN :statuses")
    Page<Payment> findByUserIdAndStatusIn(@Param("userId") Integer userId, 
                                         @Param("statuses") List<PaymentStatus> statuses, 
                                         Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.initiatedAt >= :startDate AND p.initiatedAt <= :endDate AND p.status = :status")
    Page<Payment> findByDateRangeAndStatus(@Param("startDate") Instant startDate, 
                                          @Param("endDate") Instant endDate, 
                                          @Param("status") PaymentStatus status, 
                                          Pageable pageable);
    
    // Analytics queries
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.initiatedAt >= :startDate AND p.initiatedAt <= :endDate")
    long countPaymentsByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.initiatedAt >= :startDate AND p.initiatedAt <= :endDate")
    BigDecimal getTotalAmountByStatusAndDateRange(@Param("status") PaymentStatus status, 
                                                 @Param("startDate") Instant startDate, 
                                                 @Param("endDate") Instant endDate);
    
    @Query("SELECT p.gateway, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = :status GROUP BY p.gateway")
    List<Object[]> getPaymentStatsByGateway(@Param("status") PaymentStatus status);
    
    @Query("SELECT DATE(p.initiatedAt), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.initiatedAt >= :startDate AND p.initiatedAt <= :endDate GROUP BY DATE(p.initiatedAt)")
    List<Object[]> getDailyPaymentStats(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Risk-based queries
    List<Payment> findByIsHighRiskTrue();
    Page<Payment> findByRiskScoreGreaterThan(BigDecimal riskThreshold, Pageable pageable);
    
    // Refund queries
    List<Payment> findByRefundedAmountGreaterThan(BigDecimal amount);
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND p.refundableAmount > 0")
    List<Payment> findRefundablePayments();

    // Promotion related queries
    List<Payment> findByHasPromotionTrue();
    List<Payment> findByPromotionCode(String promotionCode);
    List<Payment> findByPromotionId(Long promotionId);

    @Query("SELECT SUM(p.discountAmount) FROM Payment p WHERE p.hasPromotion = true")
    BigDecimal getTotalDiscountAmount();

    long countByHasPromotionTrue();
    long countByPromotionCode(String promotionCode);
    long countByPromotionId(Long promotionId);
}
