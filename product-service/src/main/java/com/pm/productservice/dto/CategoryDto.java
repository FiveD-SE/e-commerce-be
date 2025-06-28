package com.pm.productservice.dto;

import com.pm.productservice.model.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must not exceed 255 characters")
    private String name;
    
    private UUID parentId;
    private CategoryStatus status;
    
    @NotNull(message = "Display order is required")
    private Integer displayOrder;
    
    private String attributes;
    
    // Additional fields for display
    private String parentName;
    private List<CategoryDto> children;
    private Integer productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}