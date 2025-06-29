package com.pm.paymentservice.service;

import com.pm.paymentservice.dto.*;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {
    
    // Payment Creation and Management
    PaymentDto createPayment(CreatePaymentRequest request);
    PaymentDto getPaymentById(Long paymentId);
    PaymentDto getPaymentByReference(String paymentReference);
    PaymentDto getPaymentByGatewayTransactionId(String gatewayTransactionId);
    
    // Payment Listing and Filtering
    CollectionResponse<PaymentDto> getAllPayments(Pageable pageable);
    CollectionResponse<PaymentDto> getPaymentsByUserId(Integer userId, Pageable pageable);
    CollectionResponse<PaymentDto> getPaymentsByOrderId(Long orderId);
    CollectionResponse<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable);
    CollectionResponse<PaymentDto> getPaymentsByGateway(PaymentGateway gateway, Pageable pageable);
    CollectionResponse<PaymentDto> getPaymentsByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<PaymentSummaryDto> getPaymentSummariesByUserId(Integer userId, Pageable pageable);
    
    // Payment Processing
    PaymentDto processPayment(Long paymentId);
    PaymentDto confirmPayment(Long paymentId, String gatewayTransactionId);
    PaymentDto failPayment(Long paymentId, String reason, String errorCode);
    PaymentDto cancelPayment(Long paymentId, String reason);
    PaymentDto expirePayment(Long paymentId);
    
    // Refund Management
    PaymentTransactionDto refundPayment(Long paymentId, RefundPaymentRequest request);
    PaymentTransactionDto partialRefundPayment(Long paymentId, RefundPaymentRequest request);
    CollectionResponse<PaymentTransactionDto> getRefundsByPaymentId(Long paymentId);
    
    // Payment Search and Analytics
    CollectionResponse<PaymentDto> searchPayments(String searchTerm, Pageable pageable);
    long countPaymentsByStatus(PaymentStatus status);
    long countPaymentsByUserId(Integer userId);
    BigDecimal getTotalAmountByStatusAndDateRange(PaymentStatus status, Instant startDate, Instant endDate);
    
    // Payment Validation
    boolean canRefundPayment(Long paymentId);
    boolean canCancelPayment(Long paymentId);
    BigDecimal getRefundableAmount(Long paymentId);
    
    // Payment Status Updates
    PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status, String reason);
    
    // Webhook Processing
    void processWebhook(PaymentGateway gateway, String payload, String signature);
    
    // Scheduled Tasks
    void expireOldPayments();
    void retryFailedWebhooks();

    // Promotion Integration
    PaymentDto applyPromotionToPayment(Long paymentId, String promotionCode, Integer userId);
    PaymentDto removePromotionFromPayment(Long paymentId);
    PaymentDto validateAndApplyAutoPromotions(Long paymentId, Integer userId);
    List<PaymentDto> getPaymentsWithPromotions();
    BigDecimal getTotalDiscountAmount();
    long countPaymentsWithPromotions();
}
