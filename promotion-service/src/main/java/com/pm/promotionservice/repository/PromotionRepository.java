package com.pm.promotionservice.repository;

import com.pm.promotionservice.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Basic queries
    Optional<Promotion> findByCode(String code);
    List<Promotion> findByName(String name);
    Page<Promotion> findByName(String name, Pageable pageable);

    // Type queries
    List<Promotion> findByType(String type);
    Page<Promotion> findByType(String type, Pageable pageable);

    // Status queries
    List<Promotion> findByIsActiveTrue();
    Page<Promotion> findByIsActiveTrue(Pageable pageable);
    List<Promotion> findByIsFeaturedTrue();
    Page<Promotion> findByIsFeaturedTrue(Pageable pageable);

    // Active promotions (considering dates and stock)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0")
    List<Promotion> findActivePromotions(@Param("currentDate") Instant currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0")
    Page<Promotion> findActivePromotions(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Valid promotion by code
    @Query("SELECT p FROM Promotion p WHERE p.code = :code AND p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code, @Param("currentDate") Instant currentDate);

    // Featured active promotions
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.isFeatured = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0 " +
           "ORDER BY p.priority DESC, p.createdAt DESC")
    List<Promotion> findFeaturedActivePromotions(@Param("currentDate") Instant currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.isFeatured = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0")
    Page<Promotion> findFeaturedActivePromotions(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Auto-apply promotions
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.autoApply = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0 " +
           "ORDER BY p.priority DESC, p.createdAt ASC")
    List<Promotion> findAutoApplyPromotions(@Param("currentDate") Instant currentDate);

    // Scheduled promotions
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate > :currentDate")
    List<Promotion> findScheduledPromotions(@Param("currentDate") Instant currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate > :currentDate")
    Page<Promotion> findScheduledPromotions(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Expired promotions
    @Query("SELECT p FROM Promotion p WHERE p.endDate < :currentDate")
    List<Promotion> findExpiredPromotions(@Param("currentDate") Instant currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :currentDate")
    Page<Promotion> findExpiredPromotions(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Out of stock promotions
    @Query("SELECT p FROM Promotion p WHERE p.stock <= 0")
    List<Promotion> findOutOfStockPromotions();

    @Query("SELECT p FROM Promotion p WHERE p.stock <= 0")
    Page<Promotion> findOutOfStockPromotions(Pageable pageable);

    // Date range queries
    List<Promotion> findByCreatedAtBetween(Instant startDate, Instant endDate);
    Page<Promotion> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    List<Promotion> findByStartDateBetween(Instant startDate, Instant endDate);
    Page<Promotion> findByStartDateBetween(Instant startDate, Instant endDate, Pageable pageable);

    List<Promotion> findByEndDateBetween(Instant startDate, Instant endDate);
    Page<Promotion> findByEndDateBetween(Instant startDate, Instant endDate, Pageable pageable);

    // Search queries
    @Query("SELECT p FROM Promotion p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Promotion> searchPromotions(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Promotion> searchActivePromotions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Full-text search (MySQL specific)
    @Query(value = "SELECT * FROM promotions WHERE " +
                   "MATCH(name, description, code) AGAINST(:searchTerm IN NATURAL LANGUAGE MODE)",
           nativeQuery = true)
    List<Promotion> fullTextSearch(@Param("searchTerm") String searchTerm);

    // Count queries
    long countByIsActiveTrue();
    long countByIsFeaturedTrue();
    long countByType(String type);
    long countByCreatedBy(Integer createdBy);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.stock > 0")
    long countActivePromotions(@Param("currentDate") Instant currentDate);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.stock <= 0")
    long countOutOfStockPromotions();

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.endDate < :currentDate")
    long countExpiredPromotions(@Param("currentDate") Instant currentDate);

    // Update queries
    @Modifying
    @Query("UPDATE Promotion p SET p.stock = p.stock - 1, p.usedCount = p.usedCount + 1 WHERE p.id = :promotionId AND p.stock > 0")
    int usePromotion(@Param("promotionId") Long promotionId);

    @Modifying
    @Query("UPDATE Promotion p SET p.stock = p.stock + 1, p.usedCount = p.usedCount - 1 WHERE p.id = :promotionId AND p.usedCount > 0")
    int restorePromotionStock(@Param("promotionId") Long promotionId);

    @Modifying
    @Query("UPDATE Promotion p SET p.isActive = false WHERE p.endDate < :currentDate")
    int deactivateExpiredPromotions(@Param("currentDate") Instant currentDate);

    @Modifying
    @Query("UPDATE Promotion p SET p.isActive = false WHERE p.stock <= 0")
    int deactivateOutOfStockPromotions();

    // Analytics queries
    @Query("SELECT SUM(p.usedCount) FROM Promotion p")
    Long getTotalUsageCount();

    @Query("SELECT AVG(p.usedCount) FROM Promotion p WHERE p.usedCount > 0")
    Double getAverageUsageCount();

    @Query("SELECT p.type, COUNT(p) FROM Promotion p GROUP BY p.type")
    List<Object[]> getPromotionCountByType();

    @Query("SELECT p.type, SUM(p.usedCount) FROM Promotion p GROUP BY p.type")
    List<Object[]> getUsageCountByType();

    // Creator queries
    List<Promotion> findByCreatedBy(Integer createdBy);
    Page<Promotion> findByCreatedBy(Integer createdBy, Pageable pageable);
    List<Promotion> findByUpdatedBy(Integer updatedBy);
    Page<Promotion> findByUpdatedBy(Integer updatedBy, Pageable pageable);

    // Priority queries
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true ORDER BY p.priority DESC, p.createdAt DESC")
    List<Promotion> findActivePromotionsByPriority();

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true ORDER BY p.priority DESC, p.createdAt DESC")
    Page<Promotion> findActivePromotionsByPriority(Pageable pageable);

    // Stock queries
    @Query("SELECT p FROM Promotion p WHERE p.stock <= :threshold AND p.stock > 0")
    List<Promotion> findLowStockPromotions(@Param("threshold") Integer threshold);

    @Query("SELECT p FROM Promotion p WHERE p.stock <= :threshold AND p.stock > 0")
    Page<Promotion> findLowStockPromotions(@Param("threshold") Integer threshold, Pageable pageable);
}
