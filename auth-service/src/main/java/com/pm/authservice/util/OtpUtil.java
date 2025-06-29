package com.pm.authservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Component
@Slf4j
public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String OTP_DIGITS = "0123456789";
    private static final int OTP_LENGTH = 6;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // Phone validation pattern (supports various formats)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    /**
     * Generate a 6-digit OTP code
     */
    public String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = RANDOM.nextInt(OTP_DIGITS.length());
            otp.append(OTP_DIGITS.charAt(index));
        }
        
        String otpCode = otp.toString();
        log.debug("Generated OTP code: {} (this log should be removed in production)", otpCode);
        
        return otpCode;
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone format
     */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Remove common separators for validation
        String cleanPhone = phone.replaceAll("[\\s-().]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Format phone number for consistency
     */
    public String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }
        
        // Remove all non-digit characters except +
        String formatted = phone.replaceAll("[^+0-9]", "");
        
        // Add + prefix if it doesn't exist and the number looks international
        if (!formatted.startsWith("+") && formatted.length() > 10) {
            formatted = "+" + formatted;
        }
        
        return formatted;
    }

    /**
     * Validate OTP code format
     */
    public boolean isValidOtpCode(String otpCode) {
        if (otpCode == null || otpCode.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = otpCode.trim();
        return trimmed.length() == OTP_LENGTH && trimmed.matches("\\d{" + OTP_LENGTH + "}");
    }

    /**
     * Mask email for security (show only first few characters and domain)
     */
    public String maskEmail(String email) {
        if (email == null || !isValidEmail(email)) {
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

    /**
     * Mask phone number for security (show only last 4 digits)
     */
    public String maskPhone(String phone) {
        if (phone == null || !isValidPhone(phone)) {
            return phone;
        }
        
        String formatted = formatPhone(phone);
        if (formatted.length() < 4) {
            return "*".repeat(formatted.length());
        }
        
        String prefix = formatted.startsWith("+") ? "+" : "";
        String digits = formatted.replaceAll("[^0-9]", "");
        
        if (digits.length() < 4) {
            return prefix + "*".repeat(digits.length());
        }
        
        return prefix + "*".repeat(digits.length() - 4) + digits.substring(digits.length() - 4);
    }
} 