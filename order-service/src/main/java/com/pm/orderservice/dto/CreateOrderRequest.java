package com.pm.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    private String userPhone;

    // Shipping Address
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;

    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;

    // Billing Address (optional, can be same as shipping)
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    // Payment and Shipping preferences
    private String paymentMethod; // CREDIT_CARD, PAYPAL, BANK_TRANSFER, COD
    private String shippingMethod; // STANDARD, EXPRESS, OVERNIGHT

    // Order Items (will be fetched from cart or provided directly)
    private List<OrderItemDto> orderItems;

    // Additional Information
    private String notes;
    private Boolean isGift;
    private String giftMessage;

    // Discount
    private String discountCode;
}