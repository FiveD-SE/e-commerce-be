package com.pm.productservice.mapper;

import com.pm.productservice.dto.ProductReviewDto;
import com.pm.productservice.model.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {
    
    ProductReviewDto toDTO(ProductReview productReview);
    
    @Mapping(target = "id", ignore = true)
    ProductReview toEntity(ProductReviewDto productReviewDto);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ProductReviewDto productReviewDto, @MappingTarget ProductReview productReview);
    
    // Conversion methods
    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
    
    default Instant map(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
} 