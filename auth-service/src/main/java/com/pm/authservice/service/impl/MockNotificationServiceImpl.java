package com.pm.authservice.service.impl;

import com.pm.authservice.model.OtpToken;
import com.pm.authservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "services.user-service.mock-enabled", havingValue = "true")
@Slf4j
public class MockNotificationServiceImpl implements NotificationService {

    @Override
    public void sendEmailOtp(String email, String otpCode, String purpose) {
        log.info("MOCK EMAIL: Sending OTP to {}: {}", maskEmail(email), otpCode);
        log.info("MOCK EMAIL: Purpose: {}", purpose);
        log.info("MOCK EMAIL: OTP Code for testing: {}", otpCode);
    }

    @Override
    public void sendSmsOtp(String phone, String otpCode, String purpose) {
        log.info("MOCK SMS: Sending OTP to {}: {}", maskPhone(phone), otpCode);
        log.info("MOCK SMS: Purpose: {}", purpose);
        log.info("MOCK SMS: OTP Code for testing: {}", otpCode);
    }

    @Override
    public void sendOtp(OtpToken otpToken) {
        log.info("MOCK NOTIFICATION: Sending OTP for {} ({})", 
                otpToken.getIdentifierType(), 
                maskIdentifier(otpToken.getIdentifier(), otpToken.getIdentifierType().name()));
        log.info("MOCK NOTIFICATION: OTP Code: {}", otpToken.getOtpCode());
        log.info("MOCK NOTIFICATION: Purpose: {}", otpToken.getPurpose());
        log.info("MOCK NOTIFICATION: Expires at: {}", otpToken.getExpiresAt());
        
        switch (otpToken.getIdentifierType()) {
            case EMAIL:
                sendEmailOtp(otpToken.getIdentifier(), otpToken.getOtpCode(), otpToken.getPurpose());
                break;
            case PHONE:
                sendSmsOtp(otpToken.getIdentifier(), otpToken.getOtpCode(), otpToken.getPurpose());
                break;
            default:
                log.warn("MOCK NOTIFICATION: Unsupported identifier type: {}", otpToken.getIdentifierType());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + domain;
        }
        
        return localPart.substring(0, 2) + "*".repeat(localPart.length() - 2) + "@" + domain;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }

    private String maskIdentifier(String identifier, String type) {
        return "EMAIL".equals(type) ? maskEmail(identifier) : maskPhone(identifier);
    }
} 