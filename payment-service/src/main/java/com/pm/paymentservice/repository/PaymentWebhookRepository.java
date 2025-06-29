package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentWebhook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, Long> {
    
    // Basic finders
    Optional<PaymentWebhook> findByGatewayEventId(String gatewayEventId);
    List<PaymentWebhook> findByGatewayEventIdAndGateway(String gatewayEventId, PaymentGateway gateway);
    
    // Payment-based queries
    List<PaymentWebhook> findByPaymentPaymentId(Long paymentId);
    Page<PaymentWebhook> findByPaymentPaymentId(Long paymentId, Pageable pageable);
    
    // Gateway-based queries
    Page<PaymentWebhook> findByGateway(PaymentGateway gateway, Pageable pageable);
    List<PaymentWebhook> findByGateway(PaymentGateway gateway);
    List<PaymentWebhook> findByGatewayAndEventType(PaymentGateway gateway, String eventType);
    
    // Processing status queries
    List<PaymentWebhook> findByIsProcessedFalse();
    List<PaymentWebhook> findByIsProcessedFalseAndRetryCountLessThan(Integer maxRetries);
    Page<PaymentWebhook> findByIsProcessed(Boolean isProcessed, Pageable pageable);
    
    // Verification status queries
    List<PaymentWebhook> findByIsVerifiedFalse();
    Page<PaymentWebhook> findByIsVerified(Boolean isVerified, Pageable pageable);
    
    // Date-based queries
    Page<PaymentWebhook> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<PaymentWebhook> findByCreatedAtBetween(Instant startDate, Instant endDate);
    
    // Event type queries
    Page<PaymentWebhook> findByEventType(String eventType, Pageable pageable);
    List<PaymentWebhook> findByEventType(String eventType);
    
    // Retry queries
    List<PaymentWebhook> findByRetryCountGreaterThan(Integer retryCount);
    List<PaymentWebhook> findByLastRetryAtBefore(Instant before);
    
    // Complex queries
    @Query("SELECT pw FROM PaymentWebhook pw WHERE pw.isProcessed = false AND pw.retryCount < :maxRetries AND (pw.lastRetryAt IS NULL OR pw.lastRetryAt < :retryAfter)")
    List<PaymentWebhook> findPendingWebhooksForRetry(@Param("maxRetries") Integer maxRetries, 
                                                     @Param("retryAfter") Instant retryAfter);
    
    @Query("SELECT pw FROM PaymentWebhook pw WHERE pw.gateway = :gateway AND pw.eventType = :eventType AND pw.createdAt >= :since")
    List<PaymentWebhook> findRecentWebhooksByGatewayAndType(@Param("gateway") PaymentGateway gateway,
                                                           @Param("eventType") String eventType,
                                                           @Param("since") Instant since);
    
    // Analytics queries
    @Query("SELECT pw.gateway, pw.eventType, COUNT(pw) FROM PaymentWebhook pw GROUP BY pw.gateway, pw.eventType")
    List<Object[]> getWebhookStatsByGatewayAndType();
    
    @Query("SELECT COUNT(pw) FROM PaymentWebhook pw WHERE pw.createdAt >= :startDate AND pw.createdAt <= :endDate")
    long countWebhooksByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT DATE(pw.createdAt), COUNT(pw) FROM PaymentWebhook pw WHERE pw.createdAt >= :startDate AND pw.createdAt <= :endDate GROUP BY DATE(pw.createdAt)")
    List<Object[]> getDailyWebhookStats(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
