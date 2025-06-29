package com.pm.productservice.repository;

import com.pm.productservice.model.Category;
import com.pm.productservice.model.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    List<Category> findByStatus(CategoryStatus status);
    
    Page<Category> findByStatus(CategoryStatus status, Pageable pageable);
    
    List<Category> findByParentId(UUID parentId);
    
    List<Category> findByParentIdAndStatus(UUID parentId, CategoryStatus status);
    
    List<Category> findByParentIdIsNull();
    
    List<Category> findByParentIdIsNullAndStatus(CategoryStatus status);
    
    @Query("SELECT c FROM Category c WHERE " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:parentId IS NULL OR c.parentId = :parentId)")
    Page<Category> findWithFilters(@Param("search") String search,
                                 @Param("status") CategoryStatus status,
                                 @Param("parentId") UUID parentId,
                                 Pageable pageable);
    
    boolean existsByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}