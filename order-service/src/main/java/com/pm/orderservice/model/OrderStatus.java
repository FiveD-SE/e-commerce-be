package com.pm.orderservice.model;

public enum OrderStatus {
    PENDING,        // Order created, waiting for payment
    CONFIRMED,      // Order confirmed, payment received
    PROCESSING,     // Order being prepared
    SHIPPED,        // Order shipped
    DELIVERED,      // Order delivered
    CANCELLED,      // Order cancelled
    REFUNDED        // Order refunded
}