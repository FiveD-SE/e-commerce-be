package com.pm.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transactions")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"payment"})
@Data
@SuperBuilder
public class PaymentTransaction extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", unique = true, nullable = false, updatable = false)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "transaction_reference", unique = true, nullable = false, length = 100)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    // Gateway Information
    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;

    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse;

    @Column(name = "gateway_fee", columnDefinition = "DECIMAL(12,2)")
    private BigDecimal gatewayFee;

    // Timing
    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    // Additional Information
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Parent Transaction (for refunds)
    @Column(name = "parent_transaction_id")
    private Long parentTransactionId;

    @PrePersist
    protected void onCreate() {
        if (transactionReference == null) {
            transactionReference = generateTransactionReference();
        }
    }

    private String generateTransactionReference() {
        return type.name() + "-" + System.currentTimeMillis();
    }

    // Helper methods
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
