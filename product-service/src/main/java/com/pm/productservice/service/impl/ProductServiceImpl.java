package com.pm.productservice.service.impl;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.mapper.ProductMapper;
import com.pm.productservice.model.Product;
import com.pm.productservice.repository.ProductRepository;
import com.pm.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findAll() {
        log.info("Fetching all products");
        List<ProductDto> products = productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Integer productId) {
        log.info("Fetching product with ID: {}", productId);
        return productRepository.findById(productId)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        log.info("Saving new product: {}", productDto.getProductTitle());
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        log.info("Updating product: {}", productDto.getProductTitle());
        Product product = productMapper.toEntity(productDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public ProductDto update(Integer productId, ProductDto productDto) {
        log.info("Updating product with ID: {}", productId);
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        Product product = productMapper.toEntity(productDto);
        product.setProductId(productId);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public void deleteById(Integer productId) {
        log.info("Deleting product with ID: {}", productId);
        productRepository.deleteById(productId);
    }
}
