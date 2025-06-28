package com.pm.productservice.exception;

import java.util.UUID;

public class ProductReviewNotFoundException extends RuntimeException {
    
    public ProductReviewNotFoundException(UUID id) {
        super("Product review not found with id: " + id);
    }
    
    public ProductReviewNotFoundException(String message) {
        super(message);
    }
} 