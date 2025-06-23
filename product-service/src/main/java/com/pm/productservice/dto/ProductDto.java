package com.pm.productservice.dto;

import com.pm.productservice.model.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private String sku;
    private UUID categoryId;
    private ProductStatus status;
    private String attributes;
    private Integer quantity;
    private Integer reservedQuantity;
}