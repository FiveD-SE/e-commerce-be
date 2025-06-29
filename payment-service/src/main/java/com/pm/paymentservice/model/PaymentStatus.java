package com.pm.paymentservice.model;

public enum PaymentStatus {
    PENDING,        // Payment initiated, waiting for processing
    PROCESSING,     // Payment being processed by gateway
    COMPLETED,      // Payment successfully completed
    FAILED,         // Payment failed
    CANCELLED,      // Payment cancelled by user
    REFUNDED,       // Payment refunded
    PARTIALLY_REFUNDED, // Payment partially refunded
    EXPIRED,        // Payment expired (timeout)
    DISPUTED        // Payment disputed/chargeback
}
