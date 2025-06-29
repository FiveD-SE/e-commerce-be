package com.pm.authservice.controller;

import lombok.extern.slf4j.Slf4j;
import com.pm.authservice.dto.request.OtpRequest;
import com.pm.authservice.dto.request.OtpVerificationRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.response.ApiResponse;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.OtpResponse;
import com.pm.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and Authorization APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/generate")
    @Operation(summary = "Generate OTP", description = "Generate and send OTP to email or phone")
    public ResponseEntity<ApiResponse<OtpResponse>> generateOtp(@Valid @RequestBody OtpRequest request) {
        log.info("OTP generation request for type: {}", request.getType());
        OtpResponse response = authService.generateOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP generated successfully", response));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verify OTP and authenticate user")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        log.info("OTP verification request for type: {}", request.getType());
        AuthResponse response = authService.verifyOtpAndAuthenticate(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate Token", description = "Validate access token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get current user information from token")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        AuthResponse.UserInfo userInfo = authService.getUserFromToken(token);
        return ResponseEntity.ok(ApiResponse.success("User information retrieved", userInfo));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout and invalidate current token")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from All Devices", description = "Logout user from all devices")
    public ResponseEntity<ApiResponse<String>> logoutFromAllDevices(
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        AuthResponse.UserInfo userInfo = authService.getUserFromToken(token);
        authService.logoutFromAllDevices(userInfo.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices successfully"));
    }

    // Health check endpoint
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if auth service is running")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running"));
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }
} 