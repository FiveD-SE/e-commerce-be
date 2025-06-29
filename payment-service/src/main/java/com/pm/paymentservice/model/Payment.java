package com.pm.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"transactions", "webhooks"})
@Data
@SuperBuilder
public class Payment extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", unique = true, nullable = false, updatable = false)
    private Long paymentId;

    @Column(name = "payment_reference", unique = true, nullable = false, length = 100)
    private String paymentReference;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    // Payment Amount Information
    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "exchange_rate", columnDefinition = "DECIMAL(10,4) DEFAULT 1.0000")
    private BigDecimal exchangeRate;

    @Column(name = "amount_in_base_currency", columnDefinition = "DECIMAL(12,2)")
    private BigDecimal amountInBaseCurrency;

    // Payment Gateway Information
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 20)
    private PaymentGateway gateway;

    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;

    @Column(name = "gateway_payment_url", length = 1000)
    private String gatewayPaymentUrl;

    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse;

    // Payment Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "gateway_error_code", length = 50)
    private String gatewayErrorCode;

    // Payment Method Details
    @Column(name = "payment_method_type", length = 50)
    private String paymentMethodType; // CARD, BANK_ACCOUNT, WALLET, etc.

    @Column(name = "payment_method_details", columnDefinition = "JSON")
    private String paymentMethodDetails; // Encrypted card details, bank info, etc.

    // Timing Information
    @Column(name = "initiated_at", nullable = false)
    private Instant initiatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    // Refund Information
    @Column(name = "refunded_amount", columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal refundedAmount;

    @Column(name = "refundable_amount", columnDefinition = "DECIMAL(12,2)")
    private BigDecimal refundableAmount;

    @Column(name = "last_refund_at")
    private Instant lastRefundAt;

    // Additional Information
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "customer_ip", length = 45)
    private String customerIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Risk Assessment
    @Column(name = "risk_score", columnDefinition = "DECIMAL(3,2)")
    private BigDecimal riskScore;

    @Column(name = "is_high_risk")
    @Builder.Default
    private Boolean isHighRisk = false;

    // Relationships
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "payment", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PaymentTransaction> transactions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "payment", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PaymentWebhook> webhooks;

    @PrePersist
    protected void onCreate() {
        if (initiatedAt == null) {
            initiatedAt = Instant.now();
        }
        if (paymentReference == null) {
            paymentReference = generatePaymentReference();
        }
        if (refundedAmount == null) {
            refundedAmount = BigDecimal.ZERO;
        }
        if (exchangeRate == null) {
            exchangeRate = BigDecimal.ONE;
        }
        if (amountInBaseCurrency == null) {
            amountInBaseCurrency = amount.multiply(exchangeRate);
        }
        if (refundableAmount == null) {
            refundableAmount = amount;
        }
    }

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + orderId;
    }

    // Helper methods
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
        return isCompleted() && refundableAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public BigDecimal getRemainingRefundableAmount() {
        return refundableAmount.subtract(refundedAmount);
    }
}
