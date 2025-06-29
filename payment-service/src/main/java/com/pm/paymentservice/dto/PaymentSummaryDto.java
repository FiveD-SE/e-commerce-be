package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentStatus;
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
public class PaymentSummaryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private String paymentReference;
    private Long orderId;
    private Integer userId;
    private String userEmail;
    private BigDecimal amount;
    private String currency;
    private PaymentGateway gateway;
    private PaymentStatus status;
    private String paymentMethodType;
    private Instant initiatedAt;
    private Instant completedAt;
    private BigDecimal refundedAmount;
    
    // Computed fields
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED || status == PaymentStatus.PARTIALLY_REFUNDED;
    }
}
