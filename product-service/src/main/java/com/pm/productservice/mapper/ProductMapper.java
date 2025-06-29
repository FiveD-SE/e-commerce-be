package com.pm.productservice.mapper;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "brandId", source = "brandId")
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    ProductDto toDTO(Product product);

    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "brandId", source = "brandId")
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ProductDto productDto, @MappingTarget Product product);

    // Conversion methods
    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
    
    default Instant map(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}