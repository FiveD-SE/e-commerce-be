package com.pm.paymentservice.model;

public enum TransactionType {
    PAYMENT,        // Initial payment
    REFUND,         // Full refund
    PARTIAL_REFUND, // Partial refund
    CHARGEBACK,     // Chargeback from bank
    ADJUSTMENT,     // Manual adjustment
    FEE,            // Processing fee
    REVERSAL        // Transaction reversal
}
