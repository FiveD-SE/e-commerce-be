package com.pm.contentservice.service.impl;

import com.pm.contentservice.dto.BlogDto;
import com.pm.contentservice.dto.CreateBlogRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import com.pm.contentservice.mapper.BlogMapper;
import com.pm.contentservice.model.Blog;
import com.pm.contentservice.repository.BlogRepository;
import com.pm.contentservice.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;

    // ==================== Blog CRUD Operations ====================
    
    @Override
    public BlogDto createBlog(CreateBlogRequest request) {
        log.info("Creating blog with name: {}", request.getName());
        
        Blog blog = Blog.builder()
                .name(request.getName())
                .label(request.getLabel())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .content(request.getContent())
                .author(request.getAuthor())
                .authorId(request.getAuthorId())
                .category(request.getCategory())
                .tags(request.getTags())
                .slug(request.getSlug() != null ? request.getSlug() : generateSlug(request.getName()))
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .publishedAt(request.getPublishedAt())
                .readingTime(request.getReadingTime() != null ? request.getReadingTime() : calculateReadingTime(request.getContent()))
                .language(request.getLanguage() != null ? request.getLanguage() : "vi")
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .build();
        
        Blog savedBlog = blogRepository.save(blog);
        log.info("Blog created successfully with ID: {}", savedBlog.getId());
        
        return blogMapper.toDTO(savedBlog);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDto getBlogById(Long id) {
        log.info("Fetching blog by ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));
        return blogMapper.toDTO(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDto getBlogBySlug(String slug) {
        log.info("Fetching blog by slug: {}", slug);
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Blog not found with slug: " + slug));
        return blogMapper.toDTO(blog);
    }

    @Override
    public BlogDto updateBlog(Long id, BlogDto blogDto) {
        log.info("Updating blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));
        
        blogMapper.updateEntityFromDTO(blogDto, blog);
        Blog updatedBlog = blogRepository.save(blog);
        
        log.info("Blog updated successfully: {}", id);
        return blogMapper.toDTO(updatedBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        log.info("Deleting blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));
        
        blogRepository.delete(blog);
        log.info("Blog deleted successfully: {}", id);
    }

    // ==================== Blog Listing and Filtering ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getAllBlogs(Pageable pageable) {
        log.info("Fetching all blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findAll(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByLabel(String label, Pageable pageable) {
        log.info("Fetching blogs by label: {} with pagination: {}", label, pageable);
        Page<Blog> blogPage = blogRepository.findByLabel(label, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByAuthor(String author, Pageable pageable) {
        log.info("Fetching blogs by author: {} with pagination: {}", author, pageable);
        Page<Blog> blogPage = blogRepository.findByAuthor(author, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByAuthorId(Integer authorId, Pageable pageable) {
        log.info("Fetching blogs by author ID: {} with pagination: {}", authorId, pageable);
        Page<Blog> blogPage = blogRepository.findByAuthorId(authorId, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByCategory(String category, Pageable pageable) {
        log.info("Fetching blogs by category: {} with pagination: {}", category, pageable);
        Page<Blog> blogPage = blogRepository.findByCategory(category, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByStatus(String status, Pageable pageable) {
        log.info("Fetching blogs by status: {} with pagination: {}", status, pageable);
        Page<Blog> blogPage = blogRepository.findByStatus(status, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByLanguage(String language, Pageable pageable) {
        log.info("Fetching blogs by language: {} with pagination: {}", language, pageable);
        Page<Blog> blogPage = blogRepository.findByLanguage(language, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    // ==================== Published Blogs ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getPublishedBlogs(Pageable pageable) {
        log.info("Fetching published blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findPublishedBlogs(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getFeaturedBlogs(Pageable pageable) {
        log.info("Fetching featured blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findFeaturedPublishedBlogs(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getRecentBlogs(Pageable pageable) {
        log.info("Fetching recent blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findRecentBlogs(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getPopularBlogs(Pageable pageable) {
        log.info("Fetching popular blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findPopularBlogs(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(), 
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getMostLikedBlogs(Pageable pageable) {
        log.info("Fetching most liked blogs with pagination: {}", pageable);
        Page<Blog> blogPage = blogRepository.findMostLikedBlogs(pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(),
                blogPage.getNumber(), blogPage.getSize());
    }

    // ==================== Blog Search ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> searchBlogs(String searchTerm, Pageable pageable) {
        log.info("Searching blogs with term: {} and pagination: {}", searchTerm, pageable);
        Page<Blog> blogPage = blogRepository.searchBlogs(searchTerm, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(),
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> searchPublishedBlogs(String searchTerm, Pageable pageable) {
        log.info("Searching published blogs with term: {} and pagination: {}", searchTerm, pageable);
        Page<Blog> blogPage = blogRepository.searchPublishedBlogs(searchTerm, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(),
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogDto> fullTextSearchBlogs(String searchTerm) {
        log.info("Full-text searching blogs with term: {}", searchTerm);
        List<Blog> blogs = blogRepository.fullTextSearch(searchTerm);
        return blogs.stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Blog Actions ====================

    @Override
    public BlogDto publishBlog(Long id) {
        log.info("Publishing blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));

        blog.setIsPublished(true);
        blog.setStatus("PUBLISHED");
        blog.setPublishedAt(Instant.now());

        Blog updatedBlog = blogRepository.save(blog);
        log.info("Blog published successfully: {}", id);

        return blogMapper.toDTO(updatedBlog);
    }

    @Override
    public BlogDto unpublishBlog(Long id) {
        log.info("Unpublishing blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));

        blog.setIsPublished(false);
        blog.setStatus("DRAFT");

        Blog updatedBlog = blogRepository.save(blog);
        log.info("Blog unpublished successfully: {}", id);

        return blogMapper.toDTO(updatedBlog);
    }

    @Override
    public BlogDto archiveBlog(Long id) {
        log.info("Archiving blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));

        blog.setStatus("ARCHIVED");
        blog.setIsPublished(false);

        Blog updatedBlog = blogRepository.save(blog);
        log.info("Blog archived successfully: {}", id);

        return blogMapper.toDTO(updatedBlog);
    }

    @Override
    public BlogDto featureBlog(Long id) {
        log.info("Featuring blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));

        blog.setIsFeatured(true);

        Blog updatedBlog = blogRepository.save(blog);
        log.info("Blog featured successfully: {}", id);

        return blogMapper.toDTO(updatedBlog);
    }

    @Override
    public BlogDto unfeatureBlog(Long id) {
        log.info("Unfeaturing blog ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + id));

        blog.setIsFeatured(false);

        Blog updatedBlog = blogRepository.save(blog);
        log.info("Blog unfeatured successfully: {}", id);

        return blogMapper.toDTO(updatedBlog);
    }

    // ==================== Blog Interactions ====================

    @Override
    public BlogDto incrementViewCount(Long id) {
        log.debug("Incrementing view count for blog ID: {}", id);
        blogRepository.incrementViewCount(id);
        return getBlogById(id);
    }

    @Override
    public BlogDto incrementLikeCount(Long id) {
        log.debug("Incrementing like count for blog ID: {}", id);
        blogRepository.incrementLikeCount(id);
        return getBlogById(id);
    }

    @Override
    public BlogDto decrementLikeCount(Long id) {
        log.debug("Decrementing like count for blog ID: {}", id);
        blogRepository.decrementLikeCount(id);
        return getBlogById(id);
    }

    @Override
    public BlogDto incrementCommentCount(Long id) {
        log.debug("Incrementing comment count for blog ID: {}", id);
        blogRepository.incrementCommentCount(id);
        return getBlogById(id);
    }

    @Override
    public BlogDto decrementCommentCount(Long id) {
        log.debug("Decrementing comment count for blog ID: {}", id);
        blogRepository.decrementCommentCount(id);
        return getBlogById(id);
    }

    // ==================== Blog Analytics ====================

    @Override
    @Transactional(readOnly = true)
    public long countBlogsByStatus(String status) {
        return blogRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPublishedBlogs() {
        return blogRepository.countByIsPublishedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countFeaturedBlogs() {
        return blogRepository.countByIsFeaturedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countBlogsByAuthor(String author) {
        return blogRepository.countByAuthor(author);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBlogsByAuthorId(Integer authorId) {
        return blogRepository.countByAuthorId(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBlogsByCategory(String category) {
        return blogRepository.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalViews() {
        return blogRepository.getTotalViews();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalLikes() {
        return blogRepository.getTotalLikes();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageViews() {
        return blogRepository.getAverageViews();
    }

    // ==================== Blog Metadata ====================

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return blogRepository.findAllCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTags() {
        return blogRepository.findAllTags();
    }

    // ==================== Date Range Queries ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching blogs between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Blog> blogPage = blogRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(),
                blogPage.getNumber(), blogPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BlogDto> getBlogsByPublishedDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching blogs published between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Blog> blogPage = blogRepository.findByPublishedAtBetween(startDate, endDate, pageable);
        List<BlogDto> blogs = blogPage.getContent().stream()
                .map(blogMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(blogs, blogPage.getTotalElements(), blogPage.getTotalPages(),
                blogPage.getNumber(), blogPage.getSize());
    }

    // ==================== Utility Methods ====================

    @Override
    public String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "";
        }

        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @Override
    public Integer calculateReadingTime(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }

        // Average reading speed: 200 words per minute
        String[] words = content.trim().split("\\s+");
        int wordCount = words.length;
        int readingTime = Math.max(1, (int) Math.ceil(wordCount / 200.0));

        return readingTime;
    }
}
