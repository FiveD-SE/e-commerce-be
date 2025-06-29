package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "otp_tokens")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class OtpToken extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id", unique = true, nullable = false, updatable = false)
    private Long otpId;

    @Column(name = "identifier", nullable = false) // Email or Phone
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type", nullable = false)
    private IdentifierType identifierType;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Builder.Default
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "purpose")
    private String purpose; // LOGIN, REGISTRATION, PASSWORD_RESET, etc.

    public enum IdentifierType {
        EMAIL, PHONE
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public void incrementAttempt() {
        this.attemptCount++;
    }

    public void markAsVerified() {
        this.isVerified = true;
        this.verifiedAt = Instant.now();
    }
} 