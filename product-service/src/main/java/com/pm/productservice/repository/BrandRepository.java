package com.pm.productservice.repository;

import com.pm.productservice.model.Brand;
import com.pm.productservice.model.BrandStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    
    List<Brand> findByStatus(BrandStatus status);
    
    Page<Brand> findByStatus(BrandStatus status, Pageable pageable);
    
    @Query("SELECT b FROM Brand b WHERE " +
           "(:search IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Brand> findWithFilters(@Param("search") String search, 
                               @Param("status") BrandStatus status, 
                               Pageable pageable);
    
    boolean existsByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
} 