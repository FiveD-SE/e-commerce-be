package com.pm.productservice.service;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.ProductStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    CollectionResponse<ProductDto> findAll();
    CollectionResponse<ProductDto> findAll(Pageable pageable);
    CollectionResponse<ProductDto> findByStatus(ProductStatus status, Pageable pageable);
    CollectionResponse<ProductDto> findByCategory(UUID categoryId, Pageable pageable);
    CollectionResponse<ProductDto> findByBrand(UUID brandId, Pageable pageable);
    CollectionResponse<ProductDto> findWithFilters(String search, UUID categoryId, UUID brandId, 
                                                  ProductStatus status, BigDecimal minPrice, 
                                                  BigDecimal maxPrice, Pageable pageable);
    ProductDto findById(UUID id);
    ProductDto findBySku(String sku);
    ProductDto save(ProductDto productDto);
    ProductDto update(UUID id, ProductDto productDto);
    void deleteById(UUID id);
}