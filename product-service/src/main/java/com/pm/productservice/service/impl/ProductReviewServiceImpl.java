package com.pm.productservice.service.impl;

import com.pm.productservice.dto.ProductReviewDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.exception.ProductReviewNotFoundException;
import com.pm.productservice.mapper.ProductReviewMapper;
import com.pm.productservice.model.ProductReview;
import com.pm.productservice.model.ReviewStatus;
import com.pm.productservice.repository.ProductReviewRepository;
import com.pm.productservice.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductReviewDto> findAll() {
        log.info("Fetching all product reviews");
        List<ProductReviewDto> reviews = reviewRepository.findAll().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductReviewDto>builder()
                .data(reviews)
                .totalElements(reviews.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductReviewDto> findAll(Pageable pageable) {
        log.info("Fetching product reviews with pagination: {}", pageable);
        Page<ProductReview> reviewsPage = reviewRepository.findAll(pageable);
        List<ProductReviewDto> reviews = reviewsPage.getContent().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductReviewDto>builder()
                .data(reviews)
                .totalElements((int) reviewsPage.getTotalElements())
                .totalElements((int) reviewsPage.getTotalElements())
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductReviewDto> findByProductId(UUID productId, Pageable pageable) {
        log.info("Fetching reviews for product ID: {} with pagination: {}", productId, pageable);
        Page<ProductReview> reviewsPage = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.ACTIVE, pageable);
        List<ProductReviewDto> reviews = reviewsPage.getContent().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductReviewDto>builder()
                .data(reviews)
                .totalElements((int) reviewsPage.getTotalElements())
                .totalElements((int) reviewsPage.getTotalElements())
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductReviewDto> findByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching reviews for user ID: {} with pagination: {}", userId, pageable);
        Page<ProductReview> reviewsPage = reviewRepository.findByUserIdAndStatus(userId, ReviewStatus.ACTIVE, pageable);
        List<ProductReviewDto> reviews = reviewsPage.getContent().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductReviewDto>builder()
                .data(reviews)
                .totalElements((int) reviewsPage.getTotalElements())
                .totalElements((int) reviewsPage.getTotalElements())
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<ProductReviewDto> findWithFilters(UUID productId, UUID userId, Integer rating, ReviewStatus status, Pageable pageable) {
        log.info("Fetching reviews with filters - productId: {}, userId: {}, rating: {}, status: {}, pagination: {}", 
                productId, userId, rating, status, pageable);
        Page<ProductReview> reviewsPage = reviewRepository.findWithFilters(productId, userId, rating, status, pageable);
        List<ProductReviewDto> reviews = reviewsPage.getContent().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return CollectionResponse.<ProductReviewDto>builder()
                .data(reviews)
                .totalElements((int) reviewsPage.getTotalElements())
                .totalElements((int) reviewsPage.getTotalElements())
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewDto findById(UUID id) {
        log.info("Fetching product review with ID: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ProductReviewNotFoundException(id));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewDto findByProductAndUser(UUID productId, UUID userId) {
        log.info("Fetching review for product ID: {} and user ID: {}", productId, userId);
        ProductReview review = reviewRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductReviewNotFoundException("No review found for product " + productId + " and user " + userId));
        return reviewMapper.toDTO(review);
    }

    @Override
    public ProductReviewDto save(ProductReviewDto reviewDto) {
        log.info("Saving new product review for product: {} by user: {}", reviewDto.getProductId(), reviewDto.getUserId());
        ProductReview review = reviewMapper.toEntity(reviewDto);
        review.setStatus(ReviewStatus.ACTIVE);
        ProductReview savedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(savedReview);
    }

    @Override
    public ProductReviewDto update(UUID id, ProductReviewDto reviewDto) {
        log.info("Updating product review with ID: {}", id);
        ProductReview existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ProductReviewNotFoundException(id));
        
        reviewMapper.updateEntityFromDto(reviewDto, existingReview);
        ProductReview updatedReview = reviewRepository.save(existingReview);
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting product review with ID: {}", id);
        if (!reviewRepository.existsById(id)) {
            throw new ProductReviewNotFoundException(id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(UUID productId) {
        log.info("Calculating average rating for product ID: {}", productId);
        return reviewRepository.findAverageRatingByProductId(productId, ReviewStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCount(UUID productId) {
        log.info("Counting reviews for product ID: {}", productId);
        return reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);
    }
} 