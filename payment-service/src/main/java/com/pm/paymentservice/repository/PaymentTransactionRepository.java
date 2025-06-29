package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.PaymentStatus;
import com.pm.paymentservice.model.PaymentTransaction;
import com.pm.paymentservice.model.TransactionType;
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
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    
    // Basic finders
    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);
    Optional<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);
    
    // Payment-based queries
    List<PaymentTransaction> findByPaymentPaymentId(Long paymentId);
    Page<PaymentTransaction> findByPaymentPaymentId(Long paymentId, Pageable pageable);
    List<PaymentTransaction> findByPaymentPaymentIdAndType(Long paymentId, TransactionType type);
    
    // Type-based queries
    Page<PaymentTransaction> findByType(TransactionType type, Pageable pageable);
    List<PaymentTransaction> findByType(TransactionType type);
    
    // Status-based queries
    Page<PaymentTransaction> findByStatus(PaymentStatus status, Pageable pageable);
    List<PaymentTransaction> findByStatus(PaymentStatus status);
    long countByStatus(PaymentStatus status);
    
    // Date-based queries
    Page<PaymentTransaction> findByProcessedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<PaymentTransaction> findByProcessedAtBetween(Instant startDate, Instant endDate);
    
    // Parent transaction queries
    List<PaymentTransaction> findByParentTransactionId(Long parentTransactionId);
    
    // Amount-based queries
    Page<PaymentTransaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    // Complex queries
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.userId = :userId")
    Page<PaymentTransaction> findByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.userId = :userId AND pt.type = :type")
    List<PaymentTransaction> findByUserIdAndType(@Param("userId") Integer userId, @Param("type") TransactionType type);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.type = :type AND pt.processedAt >= :startDate AND pt.processedAt <= :endDate")
    List<PaymentTransaction> findByTypeAndDateRange(@Param("type") TransactionType type, 
                                                   @Param("startDate") Instant startDate, 
                                                   @Param("endDate") Instant endDate);
    
    // Analytics queries
    @Query("SELECT COUNT(pt) FROM PaymentTransaction pt WHERE pt.processedAt >= :startDate AND pt.processedAt <= :endDate")
    long countTransactionsByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.type = :type AND pt.status = :status AND pt.processedAt >= :startDate AND pt.processedAt <= :endDate")
    BigDecimal getTotalAmountByTypeStatusAndDateRange(@Param("type") TransactionType type,
                                                     @Param("status") PaymentStatus status,
                                                     @Param("startDate") Instant startDate, 
                                                     @Param("endDate") Instant endDate);
    
    @Query("SELECT pt.type, COUNT(pt), SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.status = :status GROUP BY pt.type")
    List<Object[]> getTransactionStatsByType(@Param("status") PaymentStatus status);
    
    // Refund-specific queries
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.type IN ('REFUND', 'PARTIAL_REFUND') AND pt.payment.paymentId = :paymentId")
    List<PaymentTransaction> findRefundsByPaymentId(@Param("paymentId") Long paymentId);
    
    @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.type IN ('REFUND', 'PARTIAL_REFUND') AND pt.payment.paymentId = :paymentId AND pt.status = 'COMPLETED'")
    BigDecimal getTotalRefundedAmountByPaymentId(@Param("paymentId") Long paymentId);
}
