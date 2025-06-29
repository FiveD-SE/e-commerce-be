package com.pm.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "payment_webhooks")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"payment"})
@Data
@SuperBuilder
public class PaymentWebhook extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_id", unique = true, nullable = false, updatable = false)
    private Long webhookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 20)
    private PaymentGateway gateway;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "gateway_event_id", length = 255)
    private String gatewayEventId;

    @Column(name = "payload", columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "headers", columnDefinition = "JSON")
    private String headers;

    @Column(name = "signature", length = 500)
    private String signature;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Builder.Default
    @Column(name = "is_processed")
    private Boolean isProcessed = false;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processing_result", columnDefinition = "TEXT")
    private String processingResult;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "last_retry_at")
    private Instant lastRetryAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Helper methods
    public void markAsProcessed(String result) {
        this.isProcessed = true;
        this.processedAt = Instant.now();
        this.processingResult = result;
    }

    public void incrementRetry() {
        this.retryCount++;
        this.lastRetryAt = Instant.now();
    }

    public boolean canRetry() {
        return retryCount < 5 && !isProcessed;
    }
}
