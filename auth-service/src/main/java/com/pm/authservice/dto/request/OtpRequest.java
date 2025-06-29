package com.pm.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier; // Email or Phone

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(EMAIL|PHONE)$", message = "Type must be either EMAIL or PHONE")
    private String type;

    private String purpose = "LOGIN"; // LOGIN, REGISTRATION, PASSWORD_RESET

} 