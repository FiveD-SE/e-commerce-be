package com.pm.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier; // Email or Phone

    @NotBlank(message = "OTP code is required")
    @Size(min = 6, max = 6, message = "OTP code must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP code must contain only digits")
    private String otpCode;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(EMAIL|PHONE)$", message = "Type must be either EMAIL or PHONE")
    private String type;

    private String deviceInfo;
    private String userAgent;

} 