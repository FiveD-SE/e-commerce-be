package com.pm.productservice.service;

import com.pm.productservice.dto.BrandDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.model.BrandStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BrandService {
    CollectionResponse<BrandDto> findAll();
    CollectionResponse<BrandDto> findAll(Pageable pageable);
    CollectionResponse<BrandDto> findByStatus(BrandStatus status, Pageable pageable);
    CollectionResponse<BrandDto> findWithFilters(String search, BrandStatus status, Pageable pageable);
    BrandDto findById(UUID id);
    BrandDto save(BrandDto brandDto);
    BrandDto update(UUID id, BrandDto brandDto);
    void deleteById(UUID id);
} 