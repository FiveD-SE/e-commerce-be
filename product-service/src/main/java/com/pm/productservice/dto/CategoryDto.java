package com.pm.productservice.dto;

import com.pm.productservice.model.CategoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;
    private UUID parentId;
    private CategoryStatus status;
    private Integer displayOrder;
    private String attributes;
}