package com.pm.paymentservice.model;

public enum PaymentGateway {
    STRIPE,         // Stripe payment gateway
    PAYPAL,         // PayPal payment gateway
    VNPAY,          // VNPay (Vietnam)
    MOMO,           // MoMo (Vietnam)
    ZALOPAY,        // ZaloPay (Vietnam)
    BANK_TRANSFER,  // Direct bank transfer
    CASH_ON_DELIVERY, // Cash on delivery
    CREDIT_CARD,    // Direct credit card processing
    DEBIT_CARD      // Direct debit card processing
}
