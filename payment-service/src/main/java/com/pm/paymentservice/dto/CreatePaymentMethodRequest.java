package com.pm.paymentservice.dto;

import com.pm.paymentservice.model.PaymentGateway;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreatePaymentMethodRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Type is required")
    private String type; // CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT, WALLET
    
    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;

    // Card Information
    private String cardNumber;
    private String cardHolderName;
    private Integer cardExpMonth;
    private Integer cardExpYear;
    private String cardCvv;

    // Bank Account Information
    private String bankAccountNumber;
    private String bankRoutingNumber;
    private String bankName;
    private String accountType; // CHECKING, SAVINGS

    // Wallet Information
    private String walletEmail;
    private String walletPhone;

    // Settings
    private Boolean setAsDefault;
    
    // Billing Address
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;
    
    private String metadata;
    private String notes;
}
