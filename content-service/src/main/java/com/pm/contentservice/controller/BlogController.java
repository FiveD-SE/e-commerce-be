package com.pm.contentservice.controller;

import com.pm.contentservice.dto.BlogDto;
import com.pm.contentservice.dto.CreateBlogRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import com.pm.contentservice.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog Management", description = "APIs for managing blogs")
public class BlogController {

    private final BlogService blogService;

    // ==================== Blog CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create a new blog", description = "Creates a new blog with the provided information")
    public ResponseEntity<BlogDto> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        log.info("Creating blog with name: {}", request.getName());
        BlogDto createdBlog = blogService.createBlog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get blog by ID", description = "Retrieves a blog by its ID")
    public ResponseEntity<BlogDto> getBlogById(@PathVariable Long id) {
        log.info("Fetching blog by ID: {}", id);
        BlogDto blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get blog by slug", description = "Retrieves a blog by its slug")
    public ResponseEntity<BlogDto> getBlogBySlug(@PathVariable String slug) {
        log.info("Fetching blog by slug: {}", slug);
        BlogDto blog = blogService.getBlogBySlug(slug);
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update blog", description = "Updates an existing blog")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable Long id, @Valid @RequestBody BlogDto blogDto) {
        log.info("Updating blog ID: {}", id);
        BlogDto updatedBlog = blogService.updateBlog(id, blogDto);
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete blog", description = "Deletes a blog by its ID")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.info("Deleting blog ID: {}", id);
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Blog Listing and Filtering ====================

    @GetMapping
    @Operation(summary = "Get all blogs", description = "Retrieves all blogs with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getAllBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getAllBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/label/{label}")
    @Operation(summary = "Get blogs by label", description = "Retrieves blogs by label with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByLabel(
            @PathVariable String label,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByLabel(label, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "Get blogs by author", description = "Retrieves blogs by author with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByAuthor(
            @PathVariable String author,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByAuthor(author, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/author-id/{authorId}")
    @Operation(summary = "Get blogs by author ID", description = "Retrieves blogs by author ID with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByAuthorId(
            @PathVariable Integer authorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByAuthorId(authorId, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get blogs by category", description = "Retrieves blogs by category with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByCategory(
            @PathVariable String category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByCategory(category, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get blogs by status", description = "Retrieves blogs by status with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByStatus(
            @PathVariable String status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByStatus(status, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "Get blogs by language", description = "Retrieves blogs by language with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByLanguage(
            @PathVariable String language,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getBlogsByLanguage(language, pageable);
        return ResponseEntity.ok(blogs);
    }

    // ==================== Published Blogs ====================

    @GetMapping("/published")
    @Operation(summary = "Get published blogs", description = "Retrieves all published blogs with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getPublishedBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "publishedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getPublishedBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured blogs", description = "Retrieves all featured published blogs with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getFeaturedBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "publishedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        CollectionResponse<BlogDto> blogs = blogService.getFeaturedBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent blogs", description = "Retrieves recent published blogs with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> getRecentBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<BlogDto> blogs = blogService.getRecentBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular blogs", description = "Retrieves popular blogs ordered by view count")
    public ResponseEntity<CollectionResponse<BlogDto>> getPopularBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<BlogDto> blogs = blogService.getPopularBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/most-liked")
    @Operation(summary = "Get most liked blogs", description = "Retrieves most liked blogs ordered by like count")
    public ResponseEntity<CollectionResponse<BlogDto>> getMostLikedBlogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        CollectionResponse<BlogDto> blogs = blogService.getMostLikedBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    // ==================== Blog Search ====================

    @GetMapping("/search")
    @Operation(summary = "Search blogs", description = "Searches blogs by term with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> searchBlogs(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BlogDto> blogs = blogService.searchBlogs(q, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/published")
    @Operation(summary = "Search published blogs", description = "Searches published blogs by term with pagination")
    public ResponseEntity<CollectionResponse<BlogDto>> searchPublishedBlogs(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "publishedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BlogDto> blogs = blogService.searchPublishedBlogs(q, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/fulltext")
    @Operation(summary = "Full-text search blogs", description = "Performs full-text search on blogs")
    public ResponseEntity<List<BlogDto>> fullTextSearchBlogs(
            @Parameter(description = "Search term") @RequestParam String q) {

        List<BlogDto> blogs = blogService.fullTextSearchBlogs(q);
        return ResponseEntity.ok(blogs);
    }

    // ==================== Blog Actions ====================

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish blog", description = "Publishes a blog")
    public ResponseEntity<BlogDto> publishBlog(@PathVariable Long id) {
        log.info("Publishing blog ID: {}", id);
        BlogDto blog = blogService.publishBlog(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish blog", description = "Unpublishes a blog")
    public ResponseEntity<BlogDto> unpublishBlog(@PathVariable Long id) {
        log.info("Unpublishing blog ID: {}", id);
        BlogDto blog = blogService.unpublishBlog(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive blog", description = "Archives a blog")
    public ResponseEntity<BlogDto> archiveBlog(@PathVariable Long id) {
        log.info("Archiving blog ID: {}", id);
        BlogDto blog = blogService.archiveBlog(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/feature")
    @Operation(summary = "Feature blog", description = "Features a blog")
    public ResponseEntity<BlogDto> featureBlog(@PathVariable Long id) {
        log.info("Featuring blog ID: {}", id);
        BlogDto blog = blogService.featureBlog(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/unfeature")
    @Operation(summary = "Unfeature blog", description = "Unfeatures a blog")
    public ResponseEntity<BlogDto> unfeatureBlog(@PathVariable Long id) {
        log.info("Unfeaturing blog ID: {}", id);
        BlogDto blog = blogService.unfeatureBlog(id);
        return ResponseEntity.ok(blog);
    }

    // ==================== Blog Interactions ====================

    @PostMapping("/{id}/view")
    @Operation(summary = "Increment view count", description = "Increments the view count of a blog")
    public ResponseEntity<BlogDto> incrementViewCount(@PathVariable Long id) {
        BlogDto blog = blogService.incrementViewCount(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Like blog", description = "Increments the like count of a blog")
    public ResponseEntity<BlogDto> likeBlog(@PathVariable Long id) {
        BlogDto blog = blogService.incrementLikeCount(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/unlike")
    @Operation(summary = "Unlike blog", description = "Decrements the like count of a blog")
    public ResponseEntity<BlogDto> unlikeBlog(@PathVariable Long id) {
        BlogDto blog = blogService.decrementLikeCount(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/comment/add")
    @Operation(summary = "Add comment", description = "Increments the comment count of a blog")
    public ResponseEntity<BlogDto> addComment(@PathVariable Long id) {
        BlogDto blog = blogService.incrementCommentCount(id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/comment/remove")
    @Operation(summary = "Remove comment", description = "Decrements the comment count of a blog")
    public ResponseEntity<BlogDto> removeComment(@PathVariable Long id) {
        BlogDto blog = blogService.decrementCommentCount(id);
        return ResponseEntity.ok(blog);
    }

    // ==================== Blog Metadata ====================

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves all blog categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = blogService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/tags")
    @Operation(summary = "Get all tags", description = "Retrieves all blog tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = blogService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    // ==================== Date Range Queries ====================

    @GetMapping("/date-range")
    @Operation(summary = "Get blogs by date range", description = "Retrieves blogs created within a date range")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BlogDto> blogs = blogService.getBlogsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/published-date-range")
    @Operation(summary = "Get blogs by published date range", description = "Retrieves blogs published within a date range")
    public ResponseEntity<CollectionResponse<BlogDto>> getBlogsByPublishedDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "publishedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CollectionResponse<BlogDto> blogs = blogService.getBlogsByPublishedDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(blogs);
    }
}
