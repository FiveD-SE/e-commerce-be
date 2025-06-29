package com.pm.paymentservice.controller;

import com.pm.paymentservice.dto.*;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentStatus;
import com.pm.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {
    
    private final PaymentService paymentService;

    // ==================== Payment Creation and Retrieval ====================
    
    @PostMapping
    @Operation(summary = "Create a new payment")
    public ResponseEntity<PaymentDto> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        log.info("Creating payment for order ID: {}", request.getOrderId());
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details by payment ID")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable @NotNull Long paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/reference/{paymentReference}")
    @Operation(summary = "Get payment details by payment reference")
    public ResponseEntity<PaymentDto> getPaymentByReference(@PathVariable @NotNull String paymentReference) {
        log.info("Fetching payment with reference: {}", paymentReference);
        return ResponseEntity.ok(paymentService.getPaymentByReference(paymentReference));
    }

    @GetMapping("/gateway-transaction/{gatewayTransactionId}")
    @Operation(summary = "Get payment details by gateway transaction ID")
    public ResponseEntity<PaymentDto> getPaymentByGatewayTransactionId(@PathVariable @NotNull String gatewayTransactionId) {
        log.info("Fetching payment with gateway transaction ID: {}", gatewayTransactionId);
        return ResponseEntity.ok(paymentService.getPaymentByGatewayTransactionId(gatewayTransactionId));
    }

    // ==================== Payment Listing and Filtering ====================
    
    @GetMapping
    @Operation(summary = "Get all payments with pagination")
    public ResponseEntity<CollectionResponse<PaymentDto>> getAllPayments(
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching all payments with pagination: {}", pageable);
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all payments for a user")
    public ResponseEntity<CollectionResponse<PaymentDto>> getPaymentsByUserId(
            @PathVariable @NotNull Integer userId,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payments for user ID: {} with pagination: {}", userId, pageable);
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId, pageable));
    }

    @GetMapping("/user/{userId}/summaries")
    @Operation(summary = "Get payment summaries for a user")
    public ResponseEntity<CollectionResponse<PaymentSummaryDto>> getPaymentSummariesByUserId(
            @PathVariable @NotNull Integer userId,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payment summaries for user ID: {} with pagination: {}", userId, pageable);
        return ResponseEntity.ok(paymentService.getPaymentSummariesByUserId(userId, pageable));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order ID")
    public ResponseEntity<CollectionResponse<PaymentDto>> getPaymentsByOrderId(@PathVariable @NotNull Long orderId) {
        log.info("Fetching payments for order ID: {}", orderId);
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<CollectionResponse<PaymentDto>> getPaymentsByStatus(
            @PathVariable @NotNull PaymentStatus status,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payments by status: {} with pagination: {}", status, pageable);
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status, pageable));
    }

    @GetMapping("/gateway/{gateway}")
    @Operation(summary = "Get payments by gateway")
    public ResponseEntity<CollectionResponse<PaymentDto>> getPaymentsByGateway(
            @PathVariable @NotNull PaymentGateway gateway,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payments by gateway: {} with pagination: {}", gateway, pageable);
        return ResponseEntity.ok(paymentService.getPaymentsByGateway(gateway, pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range")
    public ResponseEntity<CollectionResponse<PaymentDto>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payments between {} and {} with pagination: {}", startDate, endDate, pageable);
        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search payments")
    public ResponseEntity<CollectionResponse<PaymentDto>> searchPayments(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Searching payments with term: {} and pagination: {}", searchTerm, pageable);
        return ResponseEntity.ok(paymentService.searchPayments(searchTerm, pageable));
    }

    // ==================== Payment Processing ====================
    
    @PutMapping("/{paymentId}/process")
    @Operation(summary = "Process payment")
    public ResponseEntity<PaymentDto> processPayment(@PathVariable @NotNull Long paymentId) {
        log.info("Processing payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.processPayment(paymentId));
    }

    @PutMapping("/{paymentId}/confirm")
    @Operation(summary = "Confirm payment")
    public ResponseEntity<PaymentDto> confirmPayment(
            @PathVariable @NotNull Long paymentId,
            @RequestParam @NotNull String gatewayTransactionId) {
        log.info("Confirming payment ID: {} with gateway transaction ID: {}", paymentId, gatewayTransactionId);
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId, gatewayTransactionId));
    }

    @PutMapping("/{paymentId}/fail")
    @Operation(summary = "Mark payment as failed")
    public ResponseEntity<PaymentDto> failPayment(
            @PathVariable @NotNull Long paymentId,
            @RequestParam @NotNull String reason,
            @RequestParam(required = false) String errorCode) {
        log.info("Failing payment ID: {} with reason: {}", paymentId, reason);
        return ResponseEntity.ok(paymentService.failPayment(paymentId, reason, errorCode));
    }

    @PutMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel payment")
    public ResponseEntity<PaymentDto> cancelPayment(
            @PathVariable @NotNull Long paymentId,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling payment ID: {} with reason: {}", paymentId, reason);
        return ResponseEntity.ok(paymentService.cancelPayment(paymentId, reason));
    }

    @PutMapping("/{paymentId}/expire")
    @Operation(summary = "Expire payment")
    public ResponseEntity<PaymentDto> expirePayment(@PathVariable @NotNull Long paymentId) {
        log.info("Expiring payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.expirePayment(paymentId));
    }

    @PutMapping("/{paymentId}/status")
    @Operation(summary = "Update payment status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable @NotNull Long paymentId,
            @RequestParam @NotNull PaymentStatus status,
            @RequestParam(required = false) String reason) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, status, reason));
    }

    // ==================== Refund Management ====================
    
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment")
    public ResponseEntity<PaymentTransactionDto> refundPayment(
            @PathVariable @NotNull Long paymentId,
            @RequestBody @Valid RefundPaymentRequest request) {
        log.info("Processing refund for payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, request));
    }

    @PostMapping("/{paymentId}/partial-refund")
    @Operation(summary = "Partial refund payment")
    public ResponseEntity<PaymentTransactionDto> partialRefundPayment(
            @PathVariable @NotNull Long paymentId,
            @RequestBody @Valid RefundPaymentRequest request) {
        log.info("Processing partial refund for payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.partialRefundPayment(paymentId, request));
    }

    @GetMapping("/{paymentId}/refunds")
    @Operation(summary = "Get refunds by payment ID")
    public ResponseEntity<CollectionResponse<PaymentTransactionDto>> getRefundsByPaymentId(@PathVariable @NotNull Long paymentId) {
        log.info("Fetching refunds for payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.getRefundsByPaymentId(paymentId));
    }

    // ==================== Payment Analytics ====================
    
    @GetMapping("/count/status/{status}")
    @Operation(summary = "Count payments by status")
    public ResponseEntity<Long> countPaymentsByStatus(@PathVariable @NotNull PaymentStatus status) {
        log.info("Counting payments by status: {}", status);
        return ResponseEntity.ok(paymentService.countPaymentsByStatus(status));
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Count payments by user")
    public ResponseEntity<Long> countPaymentsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Counting payments for user ID: {}", userId);
        return ResponseEntity.ok(paymentService.countPaymentsByUserId(userId));
    }

    @GetMapping("/total-amount")
    @Operation(summary = "Get total amount by status and date range")
    public ResponseEntity<BigDecimal> getTotalAmountByStatusAndDateRange(
            @RequestParam @NotNull PaymentStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        log.info("Getting total amount for status: {} between {} and {}", status, startDate, endDate);
        return ResponseEntity.ok(paymentService.getTotalAmountByStatusAndDateRange(status, startDate, endDate));
    }

    // ==================== Payment Validation ====================
    
    @GetMapping("/{paymentId}/can-refund")
    @Operation(summary = "Check if payment can be refunded")
    public ResponseEntity<Boolean> canRefundPayment(@PathVariable @NotNull Long paymentId) {
        log.info("Checking if payment ID: {} can be refunded", paymentId);
        return ResponseEntity.ok(paymentService.canRefundPayment(paymentId));
    }

    @GetMapping("/{paymentId}/can-cancel")
    @Operation(summary = "Check if payment can be cancelled")
    public ResponseEntity<Boolean> canCancelPayment(@PathVariable @NotNull Long paymentId) {
        log.info("Checking if payment ID: {} can be cancelled", paymentId);
        return ResponseEntity.ok(paymentService.canCancelPayment(paymentId));
    }

    @GetMapping("/{paymentId}/refundable-amount")
    @Operation(summary = "Get refundable amount")
    public ResponseEntity<BigDecimal> getRefundableAmount(@PathVariable @NotNull Long paymentId) {
        log.info("Getting refundable amount for payment ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.getRefundableAmount(paymentId));
    }

    // ==================== Webhook Processing ====================
    
    @PostMapping("/webhooks/{gateway}")
    @Operation(summary = "Process payment webhook")
    public ResponseEntity<Void> processWebhook(
            @PathVariable @NotNull PaymentGateway gateway,
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        log.info("Processing webhook from gateway: {}", gateway);
        paymentService.processWebhook(gateway, payload, signature);
        return ResponseEntity.ok().build();
    }
}
