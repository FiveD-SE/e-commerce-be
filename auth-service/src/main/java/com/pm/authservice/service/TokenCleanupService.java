package com.pm.authservice.service;

import com.pm.authservice.repository.AuthTokenRepository;
import com.pm.authservice.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenCleanupService {

    private final AuthTokenRepository authTokenRepository;
    private final OtpTokenRepository otpTokenRepository;

    /**
     * Clean up expired auth tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredAuthTokens() {
        log.info("Starting cleanup of expired auth tokens");
        
        try {
            Instant now = Instant.now();
            long deletedCount = authTokenRepository.deleteExpiredTokens(now);
            
            if (deletedCount > 0) {
                log.info("Cleaned up {} expired auth tokens", deletedCount);
            } else {
                log.debug("No expired auth tokens found to clean up");
            }
        } catch (Exception e) {
            log.error("Error during auth token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up expired OTP tokens every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    @Transactional
    public void cleanupExpiredOtpTokens() {
        log.info("Starting cleanup of expired OTP tokens");
        
        try {
            Instant now = Instant.now();
            long deletedCount = otpTokenRepository.deleteExpiredTokens(now);
            
            if (deletedCount > 0) {
                log.info("Cleaned up {} expired OTP tokens", deletedCount);
            } else {
                log.debug("No expired OTP tokens found to clean up");
            }
        } catch (Exception e) {
            log.error("Error during OTP token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up old verified OTP tokens daily (keep for audit purposes)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Transactional
    public void cleanupOldVerifiedOtpTokens() {
        log.info("Starting cleanup of old verified OTP tokens");
        
        try {
            // Delete verified OTP tokens older than 30 days
            Instant thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 3600);
            long deletedCount = otpTokenRepository.deleteOldVerifiedTokens(thirtyDaysAgo);
            
            if (deletedCount > 0) {
                log.info("Cleaned up {} old verified OTP tokens", deletedCount);
            } else {
                log.debug("No old verified OTP tokens found to clean up");
            }
        } catch (Exception e) {
            log.error("Error during old verified OTP token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up inactive auth tokens daily
     */
    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    @Transactional
    public void cleanupInactiveAuthTokens() {
        log.info("Starting cleanup of inactive auth tokens");
        
        try {
            // Delete inactive tokens older than 7 days
            Instant sevenDaysAgo = Instant.now().minusSeconds(7 * 24 * 3600);
            long deletedCount = authTokenRepository.deleteOldInactiveTokens(sevenDaysAgo);
            
            if (deletedCount > 0) {
                log.info("Cleaned up {} inactive auth tokens", deletedCount);
            } else {
                log.debug("No inactive auth tokens found to clean up");
            }
        } catch (Exception e) {
            log.error("Error during inactive auth token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual cleanup trigger for testing or administrative purposes
     */
    public void performManualCleanup() {
        log.info("Performing manual cleanup of all expired tokens");
        
        cleanupExpiredAuthTokens();
        cleanupExpiredOtpTokens();
        cleanupOldVerifiedOtpTokens();
        cleanupInactiveAuthTokens();
        
        log.info("Manual cleanup completed");
    }
} 