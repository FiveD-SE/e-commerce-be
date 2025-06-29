package com.pm.contentservice.dto;

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
public class BlogDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String label;
    private String imageUrl;
    private String description;
    private String content;
    private String author;
    private Integer authorId;
    private String category;
    private String tags;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Instant publishedAt;
    private Integer readingTime;
    private String language;
    private String status;
    private Integer priority;
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isPublished() {
        return Boolean.TRUE.equals(this.isPublished) && "PUBLISHED".equals(this.status);
    }

    public boolean isDraft() {
        return "DRAFT".equals(this.status);
    }

    public boolean isArchived() {
        return "ARCHIVED".equals(this.status);
    }

    public String getStatusDisplay() {
        if (isPublished()) {
            return "Published";
        } else if (isDraft()) {
            return "Draft";
        } else if (isArchived()) {
            return "Archived";
        }
        return status;
    }

    public String getReadingTimeDisplay() {
        if (readingTime == null || readingTime <= 0) {
            return "Unknown";
        }
        return readingTime + " min read";
    }
}
