package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentGateway;
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
public class CreatePaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;
    
    private String paymentMethodType;
    private Long paymentMethodId; // If using saved payment method
    
    // Payment Method Details (for new payment methods)
    private String cardNumber;
    private String cardHolderName;
    private Integer cardExpMonth;
    private Integer cardExpYear;
    private String cardCvv;
    
    // Bank Account Details
    private String bankAccountNumber;
    private String bankRoutingNumber;
    private String bankName;
    
    // Wallet Details
    private String walletEmail;
    private String walletPhone;
    
    // Billing Address
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;
    
    // Additional Information
    private String description;
    private String customerIp;
    private String userAgent;
    private String returnUrl;
    private String cancelUrl;
    private String metadata;
    
    // Options
    private Boolean savePaymentMethod;
    private Boolean setAsDefault;
    private Integer timeoutMinutes;
}
