package com.pm.contentservice.repository;

import com.pm.contentservice.model.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // Basic queries
    List<Banner> findByLabel(String label);
    Page<Banner> findByLabel(String label, Pageable pageable);

    // Position queries
    List<Banner> findByPosition(String position);
    Page<Banner> findByPosition(String position, Pageable pageable);

    // Type queries
    List<Banner> findByType(String type);
    Page<Banner> findByType(String type, Pageable pageable);

    // Status queries
    List<Banner> findByIsActiveTrue();
    Page<Banner> findByIsActiveTrue(Pageable pageable);
    List<Banner> findByIsFeaturedTrue();
    Page<Banner> findByIsFeaturedTrue(Pageable pageable);

    // Active banners (considering dates)
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    List<Banner> findActiveBanners(@Param("currentDate") Instant currentDate);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    Page<Banner> findActiveBanners(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Active banners by position
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.position = :position AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate) " +
           "ORDER BY b.priority DESC, b.createdAt DESC")
    List<Banner> findActiveBannersByPosition(@Param("position") String position, @Param("currentDate") Instant currentDate);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.position = :position AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    Page<Banner> findActiveBannersByPosition(@Param("position") String position, @Param("currentDate") Instant currentDate, Pageable pageable);

    // Featured active banners
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.isFeatured = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate) " +
           "ORDER BY b.priority DESC, b.createdAt DESC")
    List<Banner> findFeaturedActiveBanners(@Param("currentDate") Instant currentDate);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.isFeatured = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    Page<Banner> findFeaturedActiveBanners(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Scheduled banners
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.startDate > :currentDate")
    List<Banner> findScheduledBanners(@Param("currentDate") Instant currentDate);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.startDate > :currentDate")
    Page<Banner> findScheduledBanners(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Expired banners
    @Query("SELECT b FROM Banner b WHERE b.endDate < :currentDate")
    List<Banner> findExpiredBanners(@Param("currentDate") Instant currentDate);

    @Query("SELECT b FROM Banner b WHERE b.endDate < :currentDate")
    Page<Banner> findExpiredBanners(@Param("currentDate") Instant currentDate, Pageable pageable);

    // Date range queries
    List<Banner> findByCreatedAtBetween(Instant startDate, Instant endDate);
    Page<Banner> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    List<Banner> findByStartDateBetween(Instant startDate, Instant endDate);
    Page<Banner> findByStartDateBetween(Instant startDate, Instant endDate, Pageable pageable);

    List<Banner> findByEndDateBetween(Instant startDate, Instant endDate);
    Page<Banner> findByEndDateBetween(Instant startDate, Instant endDate, Pageable pageable);

    // Search queries
    @Query("SELECT b FROM Banner b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Banner> searchBanners(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Popular banners
    @Query("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.clickCount DESC")
    Page<Banner> findPopularBanners(Pageable pageable);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.viewCount DESC")
    Page<Banner> findMostViewedBanners(Pageable pageable);

    // Count queries
    long countByIsActiveTrue();
    long countByIsFeaturedTrue();
    long countByPosition(String position);
    long countByType(String type);
    long countByCreatedBy(Integer createdBy);

    @Query("SELECT COUNT(b) FROM Banner b WHERE b.isActive = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    long countActiveBanners(@Param("currentDate") Instant currentDate);

    @Query("SELECT COUNT(b) FROM Banner b WHERE b.isActive = true AND b.position = :position AND " +
           "(b.startDate IS NULL OR b.startDate <= :currentDate) AND " +
           "(b.endDate IS NULL OR b.endDate >= :currentDate)")
    long countActiveBannersByPosition(@Param("position") String position, @Param("currentDate") Instant currentDate);

    // Update queries
    @Modifying
    @Query("UPDATE Banner b SET b.clickCount = b.clickCount + 1 WHERE b.id = :bannerId")
    void incrementClickCount(@Param("bannerId") Long bannerId);

    @Modifying
    @Query("UPDATE Banner b SET b.viewCount = b.viewCount + 1 WHERE b.id = :bannerId")
    void incrementViewCount(@Param("bannerId") Long bannerId);

    // Analytics queries
    @Query("SELECT SUM(b.clickCount) FROM Banner b WHERE b.isActive = true")
    Long getTotalClicks();

    @Query("SELECT SUM(b.viewCount) FROM Banner b WHERE b.isActive = true")
    Long getTotalViews();

    @Query("SELECT AVG(b.clickCount) FROM Banner b WHERE b.isActive = true")
    Double getAverageClicks();

    @Query("SELECT AVG(b.viewCount) FROM Banner b WHERE b.isActive = true")
    Double getAverageViews();

    // Positions
    @Query("SELECT DISTINCT b.position FROM Banner b WHERE b.position IS NOT NULL ORDER BY b.position")
    List<String> findAllPositions();

    // Types
    @Query("SELECT DISTINCT b.type FROM Banner b WHERE b.type IS NOT NULL ORDER BY b.type")
    List<String> findAllTypes();

    // Creator queries
    List<Banner> findByCreatedBy(Integer createdBy);
    Page<Banner> findByCreatedBy(Integer createdBy, Pageable pageable);
    List<Banner> findByUpdatedBy(Integer updatedBy);
    Page<Banner> findByUpdatedBy(Integer updatedBy, Pageable pageable);
}
