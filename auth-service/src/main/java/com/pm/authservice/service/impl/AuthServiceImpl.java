package com.pm.authservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.pm.authservice.dto.request.OtpRequest;
import com.pm.authservice.dto.request.OtpVerificationRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.OtpResponse;
import com.pm.authservice.exception.AuthenticationException;
import com.pm.authservice.exception.InvalidTokenException;
import com.pm.authservice.exception.OtpException;
import com.pm.authservice.model.AuthToken;
import com.pm.authservice.model.OtpToken;
import com.pm.authservice.repository.AuthTokenRepository;
import com.pm.authservice.repository.OtpTokenRepository;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.NotificationService;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.JwtUtil;
import com.pm.authservice.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final OtpTokenRepository otpTokenRepository;
    private final AuthTokenRepository authTokenRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final OtpUtil otpUtil;

    @Value("${otp.expiration:300}")
    private int otpExpirationSeconds;

    @Value("${otp.max-attempts:3}")
    private int maxOtpAttempts;

    @Value("${otp.resend-cooldown:60}")
    private int resendCooldownSeconds;

    @Value("${app.development-mode:true}")
    private boolean developmentMode;

    @Override
    public OtpResponse generateOtp(OtpRequest request) {
        log.info("Generating OTP for identifier: {} with type: {}", 
                maskIdentifier(request.getIdentifier(), request.getType()), request.getType());

        // Input validation
        if (request == null) {
            throw new OtpException("OTP request cannot be null");
        }
        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
            throw new OtpException("Identifier cannot be empty");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new OtpException("Identifier type cannot be empty");
        }
        if (request.getPurpose() == null || request.getPurpose().trim().isEmpty()) {
            throw new OtpException("OTP purpose cannot be empty");
        }

        // Validate identifier format
        validateIdentifier(request.getIdentifier(), request.getType());

        // Check if user exists (for login) or create user (for registration)
        if ("LOGIN".equals(request.getPurpose())) {
            validateUserExists(request.getIdentifier(), request.getType());
        }

        // Check resend cooldown
        checkResendCooldown(request.getIdentifier(), request.getType());

        // Generate OTP
        String otpCode = otpUtil.generateOtpCode();
        Instant expiresAt = Instant.now().plusSeconds(otpExpirationSeconds);

        // Save OTP token
        OtpToken otpToken = OtpToken.builder()
                .identifier(request.getIdentifier())
                .identifierType(OtpToken.IdentifierType.valueOf(request.getType()))
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .purpose(request.getPurpose())
                .build();

        otpTokenRepository.save(otpToken);

        // Send OTP
        try {
            notificationService.sendOtp(otpToken);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", maskIdentifier(request.getIdentifier(), request.getType()), e.getMessage());
            throw new OtpException("Failed to send OTP. Please try again.");
        }

        log.info("OTP generated and sent successfully for identifier: {}", 
                maskIdentifier(request.getIdentifier(), request.getType()));

        return OtpResponse.builder()
                .message("OTP sent successfully")
                .identifier(maskIdentifier(request.getIdentifier(), request.getType()))
                .type(request.getType())
                .expiresAt(expiresAt)
                .resendCooldownSeconds(resendCooldownSeconds)
                .success(true)
                // Include OTP in development mode (remove in production)
                .otpCode(developmentMode ? otpCode : null)
                .build();
    }

    @Override
    public AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request) {
        log.info("Verifying OTP for identifier: {}", maskIdentifier(request.getIdentifier(), request.getType()));

        // Input validation
        if (request == null) {
            throw new OtpException("OTP verification request cannot be null");
        }
        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
            throw new OtpException("Identifier cannot be empty");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new OtpException("Identifier type cannot be empty");
        }
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new OtpException("OTP code cannot be empty");
        }

        // Validate OTP format
        if (!otpUtil.isValidOtpCode(request.getOtpCode())) {
            throw new OtpException("Invalid OTP format");
        }

        // Find the latest OTP token
        Optional<OtpToken> optionalOtpToken = otpTokenRepository
                .findTopByIdentifierAndIdentifierTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
                        request.getIdentifier(), 
                        OtpToken.IdentifierType.valueOf(request.getType())
                );

        if (optionalOtpToken.isEmpty()) {
            throw new OtpException("No valid OTP found. Please request a new OTP.");
        }

        OtpToken otpToken = optionalOtpToken.get();

        // Check if OTP is expired
        if (otpToken.isExpired()) {
            throw new OtpException("OTP has expired. Please request a new OTP.");
        }

        // Check attempts
        if (otpToken.getAttemptCount() >= maxOtpAttempts) {
            throw new OtpException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        // Verify OTP code
        if (!otpToken.getOtpCode().equals(request.getOtpCode())) {
            otpToken.incrementAttempt();
            otpTokenRepository.save(otpToken);
            throw new OtpException("Invalid OTP code");
        }

        // Mark OTP as verified
        otpToken.markAsVerified();
        otpTokenRepository.save(otpToken);

        // Get or create user
        AuthResponse.UserInfo userInfo = getOrCreateUser(request.getIdentifier(), request.getType(), otpToken.getPurpose());

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(userInfo.getUserId(), userInfo.getEmail(), userInfo.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUserId());

        // Save token to database
        AuthToken authToken = AuthToken.builder()
                .userId(userInfo.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration()))
                .refreshTokenExpiresAt(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpiration()))
                .deviceInfo(request.getDeviceInfo())
                .userAgent(request.getUserAgent())
                .build();

        authTokenRepository.save(authToken);

        log.info("User authenticated successfully: userId={}", userInfo.getUserId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .expiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration()))
                .userId(userInfo.getUserId())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .user(userInfo)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");

        // Validate refresh token format
        if (!jwtUtil.isRefreshToken(request.getRefreshToken()) || !jwtUtil.isTokenValid(request.getRefreshToken())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Find token in database
        Optional<AuthToken> optionalAuthToken = authTokenRepository
                .findByRefreshTokenAndIsActiveTrue(request.getRefreshToken());

        if (optionalAuthToken.isEmpty()) {
            throw new InvalidTokenException("Refresh token not found or inactive");
        }

        AuthToken authToken = optionalAuthToken.get();

        // Check if refresh token is expired
        if (Instant.now().isAfter(authToken.getRefreshTokenExpiresAt())) {
            authToken.setIsActive(false);
            authTokenRepository.save(authToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        // Get user info
        AuthResponse.UserInfo userInfo = userService.getUserById(authToken.getUserId());

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(userInfo.getUserId(), userInfo.getEmail(), userInfo.getPhone());
        String newRefreshToken = jwtUtil.generateRefreshToken(userInfo.getUserId());

        // Update token in database
        authToken.setAccessToken(newAccessToken);
        authToken.setRefreshToken(newRefreshToken);
        authToken.setAccessTokenExpiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration()));
        authToken.setRefreshTokenExpiresAt(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpiration()));
        authToken.setDeviceInfo(request.getDeviceInfo());

        authTokenRepository.save(authToken);

        log.info("Token refreshed successfully for userId={}", userInfo.getUserId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .expiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration()))
                .userId(userInfo.getUserId())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .user(userInfo)
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (!jwtUtil.isAccessToken(token) || !jwtUtil.isTokenValid(token)) {
                return false;
            }

            // Check if token exists in database and is active
            return authTokenRepository.findByAccessTokenAndIsActiveTrue(token).isPresent();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public AuthResponse.UserInfo getUserFromToken(String token) {
        if (!validateToken(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        Integer userId = jwtUtil.extractUserId(token);
        return userService.getUserById(userId);
    }

    @Override
    public void logout(String token) {
        log.info("Logging out user");

        if (jwtUtil.isTokenValid(token)) {
            authTokenRepository.findByAccessTokenAndIsActiveTrue(token)
                    .ifPresent(authToken -> {
                        authToken.setIsActive(false);
                        authTokenRepository.save(authToken);
                        log.info("User logged out successfully: userId={}", authToken.getUserId());
                    });
        }
    }

    @Override
    public void logoutFromAllDevices(Integer userId) {
        log.info("Logging out user from all devices: userId={}", userId);
        authTokenRepository.deactivateAllTokensByUserId(userId);
        log.info("User logged out from all devices successfully: userId={}", userId);
    }

    private void validateIdentifier(String identifier, String type) {
        if ("EMAIL".equals(type) && !otpUtil.isValidEmail(identifier)) {
            throw new OtpException("Invalid email format");
        }
        if ("PHONE".equals(type) && !otpUtil.isValidPhone(identifier)) {
            throw new OtpException("Invalid phone format");
        }
    }

    private void validateUserExists(String identifier, String type) {
        boolean userExists = "EMAIL".equals(type) 
                ? userService.userExistsByEmail(identifier)
                : userService.userExistsByPhone(identifier);
        
        if (!userExists) {
            throw new AuthenticationException("User not found with provided " + type.toLowerCase());
        }
    }

    private void checkResendCooldown(String identifier, String type) {
        Instant cooldownThreshold = Instant.now().minusSeconds(resendCooldownSeconds);
        long recentAttempts = otpTokenRepository.countRecentUnverifiedAttempts(
                identifier, 
                OtpToken.IdentifierType.valueOf(type), 
                cooldownThreshold
        );
        
        if (recentAttempts > 0) {
            throw new OtpException("Please wait before requesting another OTP");
        }
    }

    private AuthResponse.UserInfo getOrCreateUser(String identifier, String type, String purpose) {
        try {
            // For login, get existing user
            if ("LOGIN".equals(purpose)) {
                return "EMAIL".equals(type) 
                        ? userService.getUserByEmail(identifier)
                        : userService.getUserByPhone(identifier);
            }
            
            // For registration, create user if not exists, or get existing user
            if ("REGISTRATION".equals(purpose)) {
                boolean userExists = "EMAIL".equals(type) 
                        ? userService.userExistsByEmail(identifier)
                        : userService.userExistsByPhone(identifier);
                
                if (userExists) {
                    return "EMAIL".equals(type) 
                            ? userService.getUserByEmail(identifier)
                            : userService.getUserByPhone(identifier);
                } else {
                    return "EMAIL".equals(type) 
                            ? userService.createUserByEmail(identifier)
                            : userService.createUserByPhone(identifier);
                }
            }
            
            // Default to getting existing user
            return "EMAIL".equals(type) 
                    ? userService.getUserByEmail(identifier)
                    : userService.getUserByPhone(identifier);
                    
        } catch (AuthenticationException e) {
            if ("REGISTRATION".equals(purpose)) {
                // If user not found during registration, create new user
                return "EMAIL".equals(type) 
                        ? userService.createUserByEmail(identifier)
                        : userService.createUserByPhone(identifier);
            }
            throw e;
        }
    }

    private String maskIdentifier(String identifier, String type) {
        return "EMAIL".equals(type) 
                ? otpUtil.maskEmail(identifier)
                : otpUtil.maskPhone(identifier);
    }
} 