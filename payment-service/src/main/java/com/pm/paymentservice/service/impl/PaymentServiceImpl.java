package com.pm.paymentservice.service.impl;

import com.pm.paymentservice.dto.*;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.mapper.PaymentMapper;
import com.pm.paymentservice.mapper.PaymentTransactionMapper;
import com.pm.paymentservice.model.*;
import com.pm.paymentservice.repository.*;
import com.pm.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentWebhookRepository paymentWebhookRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;

    // ==================== Payment Creation and Management ====================
    
    @Override
    public PaymentDto createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for order ID: {} and user ID: {}", request.getOrderId(), request.getUserId());
        
        // Calculate expiration time
        int timeoutMinutes = request.getTimeoutMinutes() != null ? request.getTimeoutMinutes() : 15;
        Instant expiresAt = Instant.now().plus(timeoutMinutes, ChronoUnit.MINUTES);
        
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .gateway(request.getGateway())
                .status(PaymentStatus.PENDING)
                .paymentMethodType(request.getPaymentMethodType())
                .description(request.getDescription())
                .customerIp(request.getCustomerIp())
                .userAgent(request.getUserAgent())
                .metadata(request.getMetadata())
                .expiresAt(expiresAt)
                .refundableAmount(request.getAmount())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getPaymentId());
        
        return paymentMapper.toDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long paymentId) {
        log.info("Fetching payment by ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return paymentMapper.toDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByReference(String paymentReference) {
        log.info("Fetching payment by reference: {}", paymentReference);
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + paymentReference));
        return paymentMapper.toDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByGatewayTransactionId(String gatewayTransactionId) {
        log.info("Fetching payment by gateway transaction ID: {}", gatewayTransactionId);
        Payment payment = paymentRepository.findByGatewayTransactionId(gatewayTransactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with gateway transaction ID: " + gatewayTransactionId));
        return paymentMapper.toDTO(payment);
    }

    // ==================== Payment Listing and Filtering ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getAllPayments(Pageable pageable) {
        log.info("Fetching all payments with pagination: {}", pageable);
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getPaymentsByUserId(Integer userId, Pageable pageable) {
        log.info("Fetching payments for user ID: {} with pagination: {}", userId, pageable);
        Page<Payment> paymentPage = paymentRepository.findByUserId(userId, pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getPaymentsByOrderId(Long orderId) {
        log.info("Fetching payments for order ID: {}", orderId);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        List<PaymentDto> paymentDtos = payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return new CollectionResponse<>(paymentDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        log.info("Fetching payments by status: {} with pagination: {}", status, pageable);
        Page<Payment> paymentPage = paymentRepository.findByStatus(status, pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getPaymentsByGateway(PaymentGateway gateway, Pageable pageable) {
        log.info("Fetching payments by gateway: {} with pagination: {}", gateway, pageable);
        Page<Payment> paymentPage = paymentRepository.findByGateway(gateway, pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> getPaymentsByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching payments between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Payment> paymentPage = paymentRepository.findByInitiatedAtBetween(startDate, endDate, pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentSummaryDto> getPaymentSummariesByUserId(Integer userId, Pageable pageable) {
        log.info("Fetching payment summaries for user ID: {} with pagination: {}", userId, pageable);
        Page<Payment> paymentPage = paymentRepository.findByUserId(userId, pageable);
        List<PaymentSummaryDto> paymentSummaries = paymentPage.getContent().stream()
                .map(paymentMapper::toSummaryDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentSummaryDto>builder()
                .data(paymentSummaries)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    // ==================== Payment Processing ====================

    @Override
    public PaymentDto processPayment(Long paymentId) {
        log.info("Processing payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment cannot be processed. Current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment processing started for ID: {}", paymentId);
        return paymentMapper.toDTO(updatedPayment);
    }

    @Override
    public PaymentDto confirmPayment(Long paymentId, String gatewayTransactionId) {
        log.info("Confirming payment ID: {} with gateway transaction ID: {}", paymentId, gatewayTransactionId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setGatewayTransactionId(gatewayTransactionId);
        payment.setCompletedAt(Instant.now());

        // Create successful payment transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(TransactionType.PAYMENT)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(PaymentStatus.COMPLETED)
                .gatewayTransactionId(gatewayTransactionId)
                .processedAt(Instant.now())
                .description("Payment completed")
                .build();

        paymentTransactionRepository.save(transaction);
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment confirmed successfully for ID: {}", paymentId);
        return paymentMapper.toDTO(updatedPayment);
    }

    @Override
    public PaymentDto failPayment(Long paymentId, String reason, String errorCode) {
        log.info("Failing payment ID: {} with reason: {}", paymentId, reason);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setGatewayErrorCode(errorCode);
        payment.setFailedAt(Instant.now());

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment failed for ID: {}", paymentId);
        return paymentMapper.toDTO(updatedPayment);
    }

    @Override
    public PaymentDto cancelPayment(Long paymentId, String reason) {
        log.info("Cancelling payment ID: {} with reason: {}", paymentId, reason);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (!canCancelPayment(paymentId)) {
            throw new RuntimeException("Payment cannot be cancelled. Current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(reason);
        payment.setCancelledAt(Instant.now());

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment cancelled for ID: {}", paymentId);
        return paymentMapper.toDTO(updatedPayment);
    }

    @Override
    public PaymentDto expirePayment(Long paymentId) {
        log.info("Expiring payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(PaymentStatus.EXPIRED);
        payment.setFailureReason("Payment expired");

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment expired for ID: {}", paymentId);
        return paymentMapper.toDTO(updatedPayment);
    }

    // ==================== Refund Management ====================

    @Override
    public PaymentTransactionDto refundPayment(Long paymentId, RefundPaymentRequest request) {
        log.info("Processing full refund for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (!canRefundPayment(paymentId)) {
            throw new RuntimeException("Payment cannot be refunded");
        }

        BigDecimal refundAmount = request.getAmount() != null ? request.getAmount() : payment.getRemainingRefundableAmount();

        if (refundAmount.compareTo(payment.getRemainingRefundableAmount()) > 0) {
            throw new RuntimeException("Refund amount exceeds refundable amount");
        }

        // Create refund transaction
        PaymentTransaction refundTransaction = PaymentTransaction.builder()
                .payment(payment)
                .type(refundAmount.equals(payment.getRemainingRefundableAmount()) ? TransactionType.REFUND : TransactionType.PARTIAL_REFUND)
                .amount(refundAmount)
                .currency(payment.getCurrency())
                .status(PaymentStatus.COMPLETED)
                .processedAt(Instant.now())
                .description(request.getDescription())
                .reason(request.getReason())
                .metadata(request.getMetadata())
                .notes(request.getNotes())
                .build();

        PaymentTransaction savedTransaction = paymentTransactionRepository.save(refundTransaction);

        // Update payment
        payment.setRefundedAmount(payment.getRefundedAmount().add(refundAmount));
        payment.setLastRefundAt(Instant.now());

        if (payment.getRefundedAmount().equals(payment.getAmount())) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        paymentRepository.save(payment);

        log.info("Refund processed successfully for payment ID: {}, amount: {}", paymentId, refundAmount);
        return paymentTransactionMapper.toDTO(savedTransaction);
    }

    @Override
    public PaymentTransactionDto partialRefundPayment(Long paymentId, RefundPaymentRequest request) {
        log.info("Processing partial refund for payment ID: {}", paymentId);
        return refundPayment(paymentId, request);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentTransactionDto> getRefundsByPaymentId(Long paymentId) {
        log.info("Fetching refunds for payment ID: {}", paymentId);
        List<PaymentTransaction> refunds = paymentTransactionRepository.findRefundsByPaymentId(paymentId);
        List<PaymentTransactionDto> refundDtos = refunds.stream()
                .map(paymentTransactionMapper::toDTO)
                .collect(Collectors.toList());

        return new CollectionResponse<>(refundDtos);
    }

    // ==================== Payment Search and Analytics ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentDto> searchPayments(String searchTerm, Pageable pageable) {
        log.info("Searching payments with term: {} and pagination: {}", searchTerm, pageable);
        Page<Payment> paymentPage = paymentRepository.searchPayments(searchTerm, pageable);
        List<PaymentDto> payments = paymentPage.getContent().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<PaymentDto>builder()
                .data(payments)
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPaymentsByUserId(Integer userId) {
        return paymentRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByStatusAndDateRange(PaymentStatus status, Instant startDate, Instant endDate) {
        BigDecimal total = paymentRepository.getTotalAmountByStatusAndDateRange(status, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    // ==================== Payment Validation ====================

    @Override
    @Transactional(readOnly = true)
    public boolean canRefundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return payment.canBeRefunded();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.PROCESSING;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRefundableAmount(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return payment.getRemainingRefundableAmount();
    }

    // ==================== Payment Status Updates ====================

    @Override
    public PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status, String reason) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(status);

        if (reason != null) {
            payment.setFailureReason(reason);
        }

        // Update relevant timestamps
        Instant now = Instant.now();
        switch (status) {
            case COMPLETED:
                payment.setCompletedAt(now);
                break;
            case FAILED:
                payment.setFailedAt(now);
                break;
            case CANCELLED:
                payment.setCancelledAt(now);
                break;
        }

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated from {} to {} for ID: {}", oldStatus, status, paymentId);

        return paymentMapper.toDTO(updatedPayment);
    }

    // ==================== Webhook Processing ====================

    @Override
    public void processWebhook(PaymentGateway gateway, String payload, String signature) {
        log.info("Processing webhook from gateway: {}", gateway);

        // Create webhook record
        PaymentWebhook webhook = PaymentWebhook.builder()
                .gateway(gateway)
                .payload(payload)
                .signature(signature)
                .isVerified(false)
                .isProcessed(false)
                .retryCount(0)
                .build();

        paymentWebhookRepository.save(webhook);

        // TODO: Implement actual webhook processing logic based on gateway
        log.info("Webhook saved for processing: {}", webhook.getWebhookId());
    }

    // ==================== Scheduled Tasks ====================

    @Override
    public void expireOldPayments() {
        log.info("Expiring old payments");
        List<Payment> expiredPayments = paymentRepository.findByStatusAndExpiresAtBefore(
                PaymentStatus.PENDING, Instant.now());

        for (Payment payment : expiredPayments) {
            payment.setStatus(PaymentStatus.EXPIRED);
            payment.setFailureReason("Payment expired");
        }

        paymentRepository.saveAll(expiredPayments);
        log.info("Expired {} payments", expiredPayments.size());
    }

    @Override
    public void retryFailedWebhooks() {
        log.info("Retrying failed webhooks");
        Instant retryAfter = Instant.now().minus(5, ChronoUnit.MINUTES);
        List<PaymentWebhook> webhooksToRetry = paymentWebhookRepository.findPendingWebhooksForRetry(5, retryAfter);

        for (PaymentWebhook webhook : webhooksToRetry) {
            try {
                // TODO: Implement webhook retry logic
                webhook.incrementRetry();
                paymentWebhookRepository.save(webhook);
            } catch (Exception e) {
                log.error("Failed to retry webhook {}: {}", webhook.getWebhookId(), e.getMessage());
                webhook.setErrorMessage(e.getMessage());
                webhook.incrementRetry();
                paymentWebhookRepository.save(webhook);
            }
        }

        log.info("Retried {} webhooks", webhooksToRetry.size());
    }

    // ==================== Promotion Integration Methods ====================

    @Override
    public PaymentDto applyPromotionToPayment(Long paymentId, String promotionCode, Integer userId) {
        log.info("Applying promotion {} to payment {}", promotionCode, paymentId);
        // TODO: Implement promotion application logic
        // This would call PromotionServiceClient to apply promotion
        // and update payment with promotion details
        throw new UnsupportedOperationException("Promotion integration not yet implemented");
    }

    @Override
    public PaymentDto removePromotionFromPayment(Long paymentId) {
        log.info("Removing promotion from payment {}", paymentId);
        // TODO: Implement promotion removal logic
        throw new UnsupportedOperationException("Promotion integration not yet implemented");
    }

    @Override
    public PaymentDto validateAndApplyAutoPromotions(Long paymentId, Integer userId) {
        log.info("Validating and applying auto promotions for payment {}", paymentId);
        // TODO: Implement auto promotion logic
        throw new UnsupportedOperationException("Promotion integration not yet implemented");
    }

    @Override
    public List<PaymentDto> getPaymentsWithPromotions() {
        log.info("Fetching payments with promotions");
        List<Payment> payments = paymentRepository.findByHasPromotionTrue();
        return payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalDiscountAmount() {
        log.info("Calculating total discount amount");
        return paymentRepository.getTotalDiscountAmount();
    }

    @Override
    public long countPaymentsWithPromotions() {
        log.info("Counting payments with promotions");
        return paymentRepository.countByHasPromotionTrue();
    }
}
