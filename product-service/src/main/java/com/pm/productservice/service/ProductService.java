package com.pm.productservice.service;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;

public interface ProductService {
    CollectionResponse<ProductDto> findAll();
    ProductDto findById(Integer productId);
    ProductDto save(ProductDto productDto);
    ProductDto update(ProductDto productDto);
    ProductDto update(Integer productId, ProductDto productDto);
    void deleteById(Integer productId);
}
