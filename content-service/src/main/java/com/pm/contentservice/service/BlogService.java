package com.pm.contentservice.service;

import com.pm.contentservice.dto.BlogDto;
import com.pm.contentservice.dto.CreateBlogRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface BlogService {
    
    // Blog CRUD Operations
    BlogDto createBlog(CreateBlogRequest request);
    BlogDto getBlogById(Long id);
    BlogDto getBlogBySlug(String slug);
    BlogDto updateBlog(Long id, BlogDto blogDto);
    void deleteBlog(Long id);
    
    // Blog Listing and Filtering
    CollectionResponse<BlogDto> getAllBlogs(Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByLabel(String label, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByAuthor(String author, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByAuthorId(Integer authorId, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByCategory(String category, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByStatus(String status, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByLanguage(String language, Pageable pageable);
    
    // Published Blogs
    CollectionResponse<BlogDto> getPublishedBlogs(Pageable pageable);
    CollectionResponse<BlogDto> getFeaturedBlogs(Pageable pageable);
    CollectionResponse<BlogDto> getRecentBlogs(Pageable pageable);
    CollectionResponse<BlogDto> getPopularBlogs(Pageable pageable);
    CollectionResponse<BlogDto> getMostLikedBlogs(Pageable pageable);
    
    // Blog Search
    CollectionResponse<BlogDto> searchBlogs(String searchTerm, Pageable pageable);
    CollectionResponse<BlogDto> searchPublishedBlogs(String searchTerm, Pageable pageable);
    List<BlogDto> fullTextSearchBlogs(String searchTerm);
    
    // Blog Actions
    BlogDto publishBlog(Long id);
    BlogDto unpublishBlog(Long id);
    BlogDto archiveBlog(Long id);
    BlogDto featureBlog(Long id);
    BlogDto unfeatureBlog(Long id);
    
    // Blog Interactions
    BlogDto incrementViewCount(Long id);
    BlogDto incrementLikeCount(Long id);
    BlogDto decrementLikeCount(Long id);
    BlogDto incrementCommentCount(Long id);
    BlogDto decrementCommentCount(Long id);
    
    // Blog Analytics
    long countBlogsByStatus(String status);
    long countPublishedBlogs();
    long countFeaturedBlogs();
    long countBlogsByAuthor(String author);
    long countBlogsByAuthorId(Integer authorId);
    long countBlogsByCategory(String category);
    Long getTotalViews();
    Long getTotalLikes();
    Double getAverageViews();
    
    // Blog Metadata
    List<String> getAllCategories();
    List<String> getAllTags();
    
    // Date Range Queries
    CollectionResponse<BlogDto> getBlogsByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<BlogDto> getBlogsByPublishedDateRange(Instant startDate, Instant endDate, Pageable pageable);
    
    // Utility Methods
    String generateSlug(String title);
    Integer calculateReadingTime(String content);
}
