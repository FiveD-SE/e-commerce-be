package com.pm.productservice.exception;

public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public static DuplicateResourceException forProductSku(String sku) {
        return new DuplicateResourceException("Product with SKU '" + sku + "' already exists");
    }
    
    public static DuplicateResourceException forBrandName(String name) {
        return new DuplicateResourceException("Brand with name '" + name + "' already exists");
    }
    
    public static DuplicateResourceException forCategoryName(String name) {
        return new DuplicateResourceException("Category with name '" + name + "' already exists");
    }
} 