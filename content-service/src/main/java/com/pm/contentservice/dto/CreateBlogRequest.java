package com.pm.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateBlogRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Blog name is required")
    @Size(max = 255, message = "Blog name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Blog label is required")
    @Size(max = 100, message = "Blog label must not exceed 100 characters")
    private String label;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    private String description;

    private String content;

    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String author;

    private Integer authorId;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    @Size(max = 255, message = "Meta title must not exceed 255 characters")
    private String metaTitle;

    @Size(max = 500, message = "Meta description must not exceed 500 characters")
    private String metaDescription;

    @Size(max = 500, message = "Meta keywords must not exceed 500 characters")
    private String metaKeywords;

    private Boolean isPublished;

    private Boolean isFeatured;

    private Instant publishedAt;

    private Integer readingTime;

    @Size(max = 10, message = "Language must not exceed 10 characters")
    private String language;

    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;

    private Integer priority;
}
