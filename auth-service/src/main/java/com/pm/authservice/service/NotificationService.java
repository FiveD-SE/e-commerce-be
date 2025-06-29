package com.pm.authservice.service;

import com.pm.authservice.model.OtpToken;

public interface NotificationService {

    /**
     * Send OTP via email
     */
    void sendEmailOtp(String email, String otpCode, String purpose);

    /**
     * Send OTP via SMS
     */
    void sendSmsOtp(String phone, String otpCode, String purpose);

    /**
     * Send OTP based on identifier type
     */
    void sendOtp(OtpToken otpToken);
} 