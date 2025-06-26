package com.pm.productservice.mapper;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "categoryId")
    ProductDto toDTO(Product product);

    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto productDto);
}