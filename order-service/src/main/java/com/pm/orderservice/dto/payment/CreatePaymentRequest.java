package com.pm.orderservice.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreatePaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Integer userId;
    private String userEmail;
    private BigDecimal amount;
    private String currency;
    private String gateway; // PaymentGateway enum as string
    private String paymentMethodType;
    private Long paymentMethodId;
    
    // Payment Method Details (for new payment methods)
    private String cardNumber;
    private String cardHolderName;
    private Integer cardExpMonth;
    private Integer cardExpYear;
    private String cardCvv;
    
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
