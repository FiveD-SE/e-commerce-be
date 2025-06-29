package com.pm.authservice.repository;

import com.pm.authservice.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByIdentifierAndIdentifierTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
            String identifier, OtpToken.IdentifierType identifierType);

    Optional<OtpToken> findByIdentifierAndOtpCodeAndIsVerifiedFalse(
            String identifier, String otpCode);

    @Modifying
    @Query("UPDATE OtpToken o SET o.isVerified = true WHERE o.identifier = :identifier AND o.identifierType = :identifierType")
    void markAsVerifiedByIdentifier(@Param("identifier") String identifier, 
                                   @Param("identifierType") OtpToken.IdentifierType identifierType);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :expiredBefore")
    long deleteExpiredTokens(@Param("expiredBefore") Instant expiredBefore);

    @Query("SELECT COUNT(o) FROM OtpToken o WHERE o.identifier = :identifier AND o.identifierType = :identifierType " +
           "AND o.createdAt > :since AND o.isVerified = false")
    long countRecentUnverifiedAttempts(@Param("identifier") String identifier, 
                                      @Param("identifierType") OtpToken.IdentifierType identifierType,
                                      @Param("since") Instant since);

    // Additional cleanup methods for TokenCleanupService

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.isVerified = true AND o.createdAt < :threshold")
    long deleteOldVerifiedTokens(@Param("threshold") Instant threshold);

} 