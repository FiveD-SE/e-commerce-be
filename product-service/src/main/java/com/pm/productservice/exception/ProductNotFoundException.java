package com.pm.productservice.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(UUID id) {
        super("Product not found with id: " + id);
    }
    
    public static ProductNotFoundException withSku(String sku) {
        return new ProductNotFoundException("Product not found with SKU: " + sku);
    }
    
    public ProductNotFoundException(String message) {
        super(message);
    }
} 