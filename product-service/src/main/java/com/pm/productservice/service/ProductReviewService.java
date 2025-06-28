package com.pm.productservice.service;

import com.pm.productservice.dto.ProductReviewDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.ReviewStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductReviewService {
    CollectionResponse<ProductReviewDto> findAll();
    CollectionResponse<ProductReviewDto> findAll(Pageable pageable);
    CollectionResponse<ProductReviewDto> findByProductId(UUID productId, Pageable pageable);
    CollectionResponse<ProductReviewDto> findByUserId(UUID userId, Pageable pageable);
    CollectionResponse<ProductReviewDto> findWithFilters(UUID productId, UUID userId, Integer rating, ReviewStatus status, Pageable pageable);
    ProductReviewDto findById(UUID id);
    ProductReviewDto findByProductAndUser(UUID productId, UUID userId);
    ProductReviewDto save(ProductReviewDto reviewDto);
    ProductReviewDto update(UUID id, ProductReviewDto reviewDto);
    void deleteById(UUID id);
    Double getAverageRating(UUID productId);
    Long getReviewCount(UUID productId);
} 