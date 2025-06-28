package com.pm.productservice.service.impl;

import com.pm.productservice.dto.BrandDto;
import com.pm.productservice.dto.response.collection.CollectionResponse;
import com.pm.productservice.exception.BrandNotFoundException;
import com.pm.productservice.exception.DuplicateResourceException;
import com.pm.productservice.mapper.BrandMapper;
import com.pm.productservice.model.Brand;
import com.pm.productservice.model.BrandStatus;
import com.pm.productservice.repository.BrandRepository;
import com.pm.productservice.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BrandDto> findAll() {
        log.info("Fetching all brands");
        List<BrandDto> brands = brandRepository.findAll().stream()
                .map(brandMapper::toDTO)
                .toList();
        return CollectionResponse.<BrandDto>builder()
                .data(brands)
                .totalElements(brands.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BrandDto> findAll(Pageable pageable) {
        log.info("Fetching brands with pagination: {}", pageable);
        Page<Brand> brandsPage = brandRepository.findAll(pageable);
        List<BrandDto> brands = brandsPage.getContent().stream()
                .map(brandMapper::toDTO)
                .toList();
        return CollectionResponse.<BrandDto>builder()
                .data(brands)
                .totalElements((int) brandsPage.getTotalElements())
                .totalElements((int) brandsPage.getTotalElements())
                .page(brandsPage.getNumber())
                .size(brandsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BrandDto> findByStatus(BrandStatus status, Pageable pageable) {
        log.info("Fetching brands by status: {} with pagination: {}", status, pageable);
        Page<Brand> brandsPage = brandRepository.findByStatus(status, pageable);
        List<BrandDto> brands = brandsPage.getContent().stream()
                .map(brandMapper::toDTO)
                .toList();
        return CollectionResponse.<BrandDto>builder()
                .data(brands)
                .totalElements((int) brandsPage.getTotalElements())
                .totalElements((int) brandsPage.getTotalElements())
                .page(brandsPage.getNumber())
                .size(brandsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BrandDto> findWithFilters(String search, BrandStatus status, Pageable pageable) {
        log.info("Fetching brands with filters - search: {}, status: {}, pagination: {}", search, status, pageable);
        Page<Brand> brandsPage = brandRepository.findWithFilters(search, status, pageable);
        List<BrandDto> brands = brandsPage.getContent().stream()
                .map(brandMapper::toDTO)
                .toList();
        return CollectionResponse.<BrandDto>builder()
                .data(brands)
                .totalElements((int) brandsPage.getTotalElements())
                .totalElements((int) brandsPage.getTotalElements())
                .page(brandsPage.getNumber())
                .size(brandsPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto findById(UUID id) {
        log.info("Fetching brand with ID: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        return brandMapper.toDTO(brand);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        log.info("Saving new brand: {}", brandDto.getName());
        
        // Check for duplicate name
        if (brandRepository.existsByNameIgnoreCase(brandDto.getName())) {
            throw DuplicateResourceException.forBrandName(brandDto.getName());
        }
        
        Brand brand = brandMapper.toEntity(brandDto);
        brand.setStatus(BrandStatus.ACTIVE);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(savedBrand);
    }

    @Override
    public BrandDto update(UUID id, BrandDto brandDto) {
        log.info("Updating brand with ID: {}", id);
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        
        // Check for duplicate name (excluding current brand)
        if (brandRepository.existsByNameIgnoreCaseAndIdNot(brandDto.getName(), id)) {
            throw DuplicateResourceException.forBrandName(brandDto.getName());
        }
        
        brandMapper.updateEntityFromDto(brandDto, existingBrand);
        Brand updatedBrand = brandRepository.save(existingBrand);
        return brandMapper.toDTO(updatedBrand);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting brand with ID: {}", id);
        if (!brandRepository.existsById(id)) {
            throw new BrandNotFoundException(id);
        }
        brandRepository.deleteById(id);
    }
} 