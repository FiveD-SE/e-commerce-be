package com.pm.productservice.service.impl;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.mapper.ProductMapper;
import com.pm.productservice.model.Product;
import com.pm.productservice.model.ProductInventory;
import com.pm.productservice.model.ProductStatus;
import com.pm.productservice.repository.ProductInventoryRepository;
import com.pm.productservice.repository.ProductRepository;
import com.pm.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findAll() {
        log.info("Fetching all products");
        List<ProductDto> products = productRepository.findAll().stream()
                .map(this::toDtoWithInventory)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements(products.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(UUID id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDtoWithInventory(product);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        log.info("Saving new product: {}", productDto.getName());
        Product product = productMapper.toEntity(productDto);
        product.setStatus(ProductStatus.active);
        Product savedProduct = productRepository.save(product);

        ProductInventory inventory = ProductInventory.builder()
                .productId(savedProduct.getId())
                .quantity(productDto.getQuantity() != null ? productDto.getQuantity() : 0)
                .reservedQuantity(productDto.getReservedQuantity() != null ? productDto.getReservedQuantity() : 0)
                .updatedAt(Instant.now())
                .build();
        productInventoryRepository.save(inventory);

        return toDtoWithInventory(savedProduct);
    }

    @Override
    public ProductDto update(UUID id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        Product product = productMapper.toEntity(productDto);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);

        ProductInventory inventory = productInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setQuantity(productDto.getQuantity() != null ? productDto.getQuantity() : inventory.getQuantity());
        inventory.setReservedQuantity(productDto.getReservedQuantity() != null ? productDto.getReservedQuantity() : inventory.getReservedQuantity());
        inventory.setUpdatedAt(Instant.now());
        productInventoryRepository.save(inventory);

        return toDtoWithInventory(updatedProduct);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
        productInventoryRepository.deleteById(id);
    }

    private ProductDto toDtoWithInventory(Product product) {
        ProductDto dto = productMapper.toDTO(product);
        productInventoryRepository.findById(product.getId()).ifPresent(inventory -> {
            dto.setQuantity(inventory.getQuantity());
            dto.setReservedQuantity(inventory.getReservedQuantity());
        });
        return dto;
    }
}