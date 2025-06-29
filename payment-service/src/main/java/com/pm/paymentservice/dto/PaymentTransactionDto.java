package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentStatus;
import com.pm.paymentservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentTransactionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long transactionId;
    private String transactionReference;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;
    
    private PaymentStatus status;

    // Gateway Information
    private String gatewayTransactionId;
    private String gatewayResponse;
    private BigDecimal gatewayFee;

    // Timing
    private Instant processedAt;
    private Instant settledAt;

    // Additional Information
    private String description;
    private String reason;
    private String metadata;
    private String notes;

    // Parent Transaction (for refunds)
    private Long parentTransactionId;
    
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isRefund() {
        return type == TransactionType.REFUND || type == TransactionType.PARTIAL_REFUND;
    }

    public boolean isPayment() {
        return type == TransactionType.PAYMENT;
    }

    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
}
