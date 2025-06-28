package com.pm.productservice.repository;

import com.pm.productservice.model.Product;
import com.pm.productservice.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    List<Product> findByStatus(ProductStatus status);
    
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    List<Product> findByCategoryId(UUID categoryId);
    
    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
    
    List<Product> findByBrandId(UUID brandId);
    
    Page<Product> findByBrandId(UUID brandId, Pageable pageable);
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, UUID id);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:brandId IS NULL OR p.brandId = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findWithFilters(@Param("search") String search,
                                @Param("categoryId") UUID categoryId,
                                @Param("brandId") UUID brandId,
                                @Param("status") ProductStatus status,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.categoryId = :categoryId AND p.status = :status")
    Long countByCategoryIdAndStatus(@Param("categoryId") UUID categoryId, @Param("status") ProductStatus status);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brandId = :brandId AND p.status = :status")
    Long countByBrandIdAndStatus(@Param("brandId") UUID brandId, @Param("status") ProductStatus status);
}