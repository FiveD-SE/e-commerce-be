package com.pm.contentservice.repository;

import com.pm.contentservice.model.Blog;
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
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Basic queries
    Optional<Blog> findBySlug(String slug);
    List<Blog> findByLabel(String label);
    Page<Blog> findByLabel(String label, Pageable pageable);

    // Author queries
    List<Blog> findByAuthor(String author);
    Page<Blog> findByAuthor(String author, Pageable pageable);
    List<Blog> findByAuthorId(Integer authorId);
    Page<Blog> findByAuthorId(Integer authorId, Pageable pageable);

    // Category queries
    List<Blog> findByCategory(String category);
    Page<Blog> findByCategory(String category, Pageable pageable);

    // Status queries
    List<Blog> findByStatus(String status);
    Page<Blog> findByStatus(String status, Pageable pageable);
    List<Blog> findByIsPublishedTrue();
    Page<Blog> findByIsPublishedTrue(Pageable pageable);
    List<Blog> findByIsFeaturedTrue();
    Page<Blog> findByIsFeaturedTrue(Pageable pageable);

    // Published blogs
    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED'")
    List<Blog> findPublishedBlogs();

    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED'")
    Page<Blog> findPublishedBlogs(Pageable pageable);

    // Featured published blogs
    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED' AND b.isFeatured = true")
    List<Blog> findFeaturedPublishedBlogs();

    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED' AND b.isFeatured = true")
    Page<Blog> findFeaturedPublishedBlogs(Pageable pageable);

    // Date range queries
    List<Blog> findByCreatedAtBetween(Instant startDate, Instant endDate);
    Page<Blog> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<Blog> findByPublishedAtBetween(Instant startDate, Instant endDate);
    Page<Blog> findByPublishedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    // Search queries
    @Query("SELECT b FROM Blog b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Blog> searchBlogs(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE " +
           "b.isPublished = true AND b.status = 'PUBLISHED' AND (" +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Blog> searchPublishedBlogs(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Full-text search (MySQL specific)
    @Query(value = "SELECT * FROM blogs WHERE " +
                   "MATCH(name, description, content, tags) AGAINST(:searchTerm IN NATURAL LANGUAGE MODE)",
           nativeQuery = true)
    List<Blog> fullTextSearch(@Param("searchTerm") String searchTerm);

    // Popular blogs
    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED' ORDER BY b.viewCount DESC")
    Page<Blog> findPopularBlogs(Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED' ORDER BY b.likeCount DESC")
    Page<Blog> findMostLikedBlogs(Pageable pageable);

    // Recent blogs
    @Query("SELECT b FROM Blog b WHERE b.isPublished = true AND b.status = 'PUBLISHED' ORDER BY b.publishedAt DESC")
    Page<Blog> findRecentBlogs(Pageable pageable);

    // Count queries
    long countByStatus(String status);
    long countByIsPublishedTrue();
    long countByIsFeaturedTrue();
    long countByAuthor(String author);
    long countByAuthorId(Integer authorId);
    long countByCategory(String category);

    // Update queries
    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :blogId")
    void incrementViewCount(@Param("blogId") Long blogId);

    @Modifying
    @Query("UPDATE Blog b SET b.likeCount = b.likeCount + 1 WHERE b.id = :blogId")
    void incrementLikeCount(@Param("blogId") Long blogId);

    @Modifying
    @Query("UPDATE Blog b SET b.likeCount = b.likeCount - 1 WHERE b.id = :blogId AND b.likeCount > 0")
    void decrementLikeCount(@Param("blogId") Long blogId);

    @Modifying
    @Query("UPDATE Blog b SET b.commentCount = b.commentCount + 1 WHERE b.id = :blogId")
    void incrementCommentCount(@Param("blogId") Long blogId);

    @Modifying
    @Query("UPDATE Blog b SET b.commentCount = b.commentCount - 1 WHERE b.id = :blogId AND b.commentCount > 0")
    void decrementCommentCount(@Param("blogId") Long blogId);

    // Analytics queries
    @Query("SELECT SUM(b.viewCount) FROM Blog b WHERE b.isPublished = true")
    Long getTotalViews();

    @Query("SELECT SUM(b.likeCount) FROM Blog b WHERE b.isPublished = true")
    Long getTotalLikes();

    @Query("SELECT AVG(b.viewCount) FROM Blog b WHERE b.isPublished = true")
    Double getAverageViews();

    // Categories
    @Query("SELECT DISTINCT b.category FROM Blog b WHERE b.category IS NOT NULL AND b.isPublished = true ORDER BY b.category")
    List<String> findAllCategories();

    // Tags
    @Query("SELECT DISTINCT b.tags FROM Blog b WHERE b.tags IS NOT NULL AND b.isPublished = true")
    List<String> findAllTags();

    // Language queries
    List<Blog> findByLanguage(String language);
    Page<Blog> findByLanguage(String language, Pageable pageable);
}
