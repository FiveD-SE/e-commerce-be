package com.pm.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RefundPaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal amount; // If null, full refund
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String description;
    private String metadata;
    private String notes;
    
    // Admin information
    private String adminUserId;
    private String adminNotes;
}
