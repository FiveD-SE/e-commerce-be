package com.pm.productservice.service;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;

import java.util.UUID;

public interface ProductService {
    CollectionResponse<ProductDto> findAll();
    ProductDto findById(UUID id);
    ProductDto save(ProductDto productDto);
    ProductDto update(UUID id, ProductDto productDto);
    void deleteById(UUID id);
}