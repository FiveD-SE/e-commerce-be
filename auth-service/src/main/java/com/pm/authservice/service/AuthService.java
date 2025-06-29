package com.pm.authservice.service;

import com.pm.authservice.dto.request.OtpRequest;
import com.pm.authservice.dto.request.OtpVerificationRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.OtpResponse;

public interface AuthService {

    /**
     * Generate and send OTP to email or phone
     */
    OtpResponse generateOtp(OtpRequest request);

    /**
     * Verify OTP and authenticate user
     */
    AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Validate access token
     */
    boolean validateToken(String token);

    /**
     * Get user info from token
     */
    AuthResponse.UserInfo getUserFromToken(String token);

    /**
     * Logout and invalidate tokens
     */
    void logout(String token);

    /**
     * Logout from all devices for a user
     */
    void logoutFromAllDevices(Integer userId);
} 