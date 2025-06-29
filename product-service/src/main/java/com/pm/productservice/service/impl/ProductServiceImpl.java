package com.pm.productservice.service.impl;

import com.pm.productservice.dto.ProductDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.exception.DuplicateResourceException;
import com.pm.productservice.exception.ProductNotFoundException;
import com.pm.productservice.mapper.ProductMapper;
import com.pm.productservice.model.Product;
import com.pm.productservice.model.ProductInventory;
import com.pm.productservice.model.ProductStatus;
import com.pm.productservice.model.ReviewStatus;
import com.pm.productservice.repository.BrandRepository;
import com.pm.productservice.repository.CategoryRepository;
import com.pm.productservice.repository.ProductInventoryRepository;
import com.pm.productservice.repository.ProductRepository;
import com.pm.productservice.repository.ProductReviewRepository;
import com.pm.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ProductReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findAll() {
        log.info("Fetching all products");
        List<ProductDto> products = productRepository.findAll().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements(products.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findAll(Pageable pageable) {
        log.info("Fetching products with pagination: {}", pageable);
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<ProductDto> products = productsPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements((int) productsPage.getTotalElements())
                .totalElements((int) productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findByStatus(ProductStatus status, Pageable pageable) {
        log.info("Fetching products by status: {} with pagination: {}", status, pageable);
        Page<Product> productsPage = productRepository.findByStatus(status, pageable);
        List<ProductDto> products = productsPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements((int) productsPage.getTotalElements())
                .totalElements((int) productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findByCategory(UUID categoryId, Pageable pageable) {
        log.info("Fetching products by category ID: {} with pagination: {}", categoryId, pageable);
        Page<Product> productsPage = productRepository.findByCategoryId(categoryId, pageable);
        List<ProductDto> products = productsPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements((int) productsPage.getTotalElements())
                .totalElements((int) productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findByBrand(UUID brandId, Pageable pageable) {
        log.info("Fetching products by brand ID: {} with pagination: {}", brandId, pageable);
        Page<Product> productsPage = productRepository.findByBrandId(brandId, pageable);
        List<ProductDto> products = productsPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements((int) productsPage.getTotalElements())
                .totalElements((int) productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductDto> findWithFilters(String search, UUID categoryId, UUID brandId, 
                                                        ProductStatus status, BigDecimal minPrice, 
                                                        BigDecimal maxPrice, Pageable pageable) {
        log.info("Fetching products with filters - search: {}, categoryId: {}, brandId: {}, status: {}, minPrice: {}, maxPrice: {}, pagination: {}", 
                search, categoryId, brandId, status, minPrice, maxPrice, pageable);
        Page<Product> productsPage = productRepository.findWithFilters(search, categoryId, brandId, status, minPrice, maxPrice, pageable);
        List<ProductDto> products = productsPage.getContent().stream()
                .map(this::toDtoWithExtendedInfo)
                .toList();
        return CollectionResponse.<ProductDto>builder()
                .data(products)
                .totalElements((int) productsPage.getTotalElements())
                .totalElements((int) productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(UUID id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDtoWithExtendedInfo(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findBySku(String sku) {
        log.info("Fetching product with SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));
        return toDtoWithExtendedInfo(product);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        log.info("Saving new product: {}", productDto.getName());
        
        // Check for duplicate SKU
        if (productRepository.existsBySku(productDto.getSku())) {
            throw DuplicateResourceException.forProductSku(productDto.getSku());
        }
        
        Product product = productMapper.toEntity(productDto);
        product.setStatus(ProductStatus.active);
        Product savedProduct = productRepository.save(product);

        // Create inventory record
        ProductInventory inventory = ProductInventory.builder()
                .productId(savedProduct.getId())
                .quantity(productDto.getQuantity() != null ? productDto.getQuantity() : 0)
                .reservedQuantity(productDto.getReservedQuantity() != null ? productDto.getReservedQuantity() : 0)
                .updatedAt(Instant.now())
                .build();
        productInventoryRepository.save(inventory);

        return toDtoWithExtendedInfo(savedProduct);
    }

    @Override
    public ProductDto update(UUID id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Check for duplicate SKU (excluding current product)
        if (productRepository.existsBySkuAndIdNot(productDto.getSku(), id)) {
            throw DuplicateResourceException.forProductSku(productDto.getSku());
        }
        
        Product product = productMapper.toEntity(productDto);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);

        // Update inventory
        ProductInventory inventory = productInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + id));
        inventory.setQuantity(productDto.getQuantity() != null ? productDto.getQuantity() : inventory.getQuantity());
        inventory.setReservedQuantity(productDto.getReservedQuantity() != null ? productDto.getReservedQuantity() : inventory.getReservedQuantity());
        inventory.setUpdatedAt(Instant.now());
        productInventoryRepository.save(inventory);

        return toDtoWithExtendedInfo(updatedProduct);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        productInventoryRepository.deleteById(id);
    }

    private ProductDto toDtoWithExtendedInfo(Product product) {
        ProductDto dto = productMapper.toDTO(product);
        
        // Add inventory information
        productInventoryRepository.findById(product.getId()).ifPresent(inventory -> {
            dto.setQuantity(inventory.getQuantity());
            dto.setReservedQuantity(inventory.getReservedQuantity());
        });
        
        // Add category name
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId()).ifPresent(category -> 
                dto.setCategoryName(category.getName())
            );
        }
        
        // Add brand name
        if (product.getBrandId() != null) {
            brandRepository.findById(product.getBrandId()).ifPresent(brand -> 
                dto.setBrandName(brand.getName())
            );
        }
        
        // Add review statistics
        Double averageRating = reviewRepository.findAverageRatingByProductId(product.getId(), ReviewStatus.ACTIVE);
        Long reviewCount = reviewRepository.countByProductIdAndStatus(product.getId(), ReviewStatus.ACTIVE);
        dto.setAverageRating(averageRating != null ? averageRating : 0.0);
        dto.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
        
        return dto;
    }
}