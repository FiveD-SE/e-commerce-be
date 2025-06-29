package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private String paymentReference;
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    // Payment Amount Information
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;
    
    private BigDecimal exchangeRate;
    private BigDecimal amountInBaseCurrency;

    // Payment Gateway Information
    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;
    
    private String gatewayTransactionId;
    private String gatewayPaymentUrl;
    private String gatewayResponse;

    // Payment Status
    private PaymentStatus status;
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

    // Relationships
    private Set<PaymentTransactionDto> transactions;
    
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }

    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED || status == PaymentStatus.PARTIALLY_REFUNDED;
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
