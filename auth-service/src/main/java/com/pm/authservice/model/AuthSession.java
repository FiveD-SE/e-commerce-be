package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "auth_sessions")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AuthSession extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", unique = true, nullable = false, updatable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @PrePersist
    protected void onCreate() {
        this.lastActivityAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastActivityAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public void deactivate() {
        this.isActive = false;
    }
} 