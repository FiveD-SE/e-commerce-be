package com.pm.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpResponse {

    private String message;
    private String identifier;
    private String type;
    private Instant expiresAt;
    private Integer resendCooldownSeconds;
    private Boolean success = true;

    // For development/testing only - remove in production
    private String otpCode;

} 