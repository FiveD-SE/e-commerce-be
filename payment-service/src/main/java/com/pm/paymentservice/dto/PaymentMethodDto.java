package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentGateway;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentMethodDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long methodId;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;
    
    private String gatewayMethodId;

    // Card Information (masked for security)
    private String cardLastFour;
    private String cardBrand;
    private Integer cardExpMonth;
    private Integer cardExpYear;
    private String cardHolderName;

    // Bank Account Information (masked)
    private String bankName;
    private String accountLastFour;
    private String accountType;

    // Wallet Information (masked)
    private String walletEmail;
    private String walletPhone;

    // Status and Settings
    private Boolean isActive;
    private Boolean isDefault;
    private Boolean isVerified;

    // Additional Information
    private String billingAddress;
    private String metadata;
    private String notes;
    
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isCard() {
        return "CREDIT_CARD".equals(type) || "DEBIT_CARD".equals(type);
    }

    public boolean isBankAccount() {
        return "BANK_ACCOUNT".equals(type);
    }

    public boolean isWallet() {
        return "WALLET".equals(type);
    }

    public boolean isExpired() {
        if (!isCard() || cardExpMonth == null || cardExpYear == null) {
            return false;
        }
        
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate expiry = java.time.LocalDate.of(cardExpYear, cardExpMonth, 1)
                .plusMonths(1).minusDays(1);
        
        return now.isAfter(expiry);
    }

    public String getMaskedNumber() {
        if (isCard() && cardLastFour != null) {
            return "**** **** **** " + cardLastFour;
        } else if (isBankAccount() && accountLastFour != null) {
            return "****" + accountLastFour;
        } else if (isWallet() && walletEmail != null) {
            String email = walletEmail;
            int atIndex = email.indexOf('@');
            if (atIndex > 2) {
                return email.substring(0, 2) + "***" + email.substring(atIndex);
            }
            return "***" + email.substring(atIndex);
        }
        return "****";
    }

    public String getDisplayName() {
        if (isCard()) {
            return cardBrand + " " + getMaskedNumber();
        } else if (isBankAccount()) {
            return bankName + " " + getMaskedNumber();
        } else if (isWallet()) {
            return gateway.name() + " " + getMaskedNumber();
        }
        return name;
    }
}
