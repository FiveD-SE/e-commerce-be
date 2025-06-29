package com.pm.orderservice.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private String paymentReference;
    private Long orderId;
    private Integer userId;
    private String userEmail;

    // Payment Amount Information
    private BigDecimal amount;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal amountInBaseCurrency;

    // Payment Gateway Information
    private String gateway; // PaymentGateway enum as string
    private String gatewayTransactionId;
    private String gatewayPaymentUrl;
    private String gatewayResponse;

    // Payment Status
    private String status; // PaymentStatus enum as string
    private String failureReason;
    private String gatewayErrorCode;

    // Payment Method Details
    private String paymentMethodType;
    private String paymentMethodDetails;

    // Timing Information
    private Instant initiatedAt;
    private Instant expiresAt;
    private Instant completedAt;
    private Instant failedAt;
    private Instant cancelledAt;

    // Refund Information
    private BigDecimal refundedAmount;
    private BigDecimal refundableAmount;
    private Instant lastRefundAt;

    // Additional Information
    private String description;
    private String customerIp;
    private String userAgent;
    private String metadata;
    private String notes;

    // Risk Assessment
    private BigDecimal riskScore;
    private Boolean isHighRisk;
    
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public boolean isRefunded() {
        return "REFUNDED".equals(status) || "PARTIALLY_REFUNDED".equals(status);
    }

    public boolean canBeRefunded() {
        return isCompleted() && refundableAmount != null && refundableAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public BigDecimal getRemainingRefundableAmount() {
        if (refundableAmount == null || refundedAmount == null) {
            return BigDecimal.ZERO;
        }
        return refundableAmount.subtract(refundedAmount);
    }
}
