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
public class CreateBannerRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Banner name is required")
    @Size(max = 255, message = "Banner name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Banner label is required")
    @Size(max = 100, message = "Banner label must not exceed 100 characters")
    private String label;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Subtitle must not exceed 255 characters")
    private String subtitle;

    private String description;

    @Size(max = 500, message = "Link URL must not exceed 500 characters")
    private String linkUrl;

    @Size(max = 100, message = "Link text must not exceed 100 characters")
    private String linkText;

    @Size(max = 50, message = "Position must not exceed 50 characters")
    private String position;

    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    @Size(max = 20, message = "Target must not exceed 20 characters")
    private String target;

    private Boolean isActive;

    private Boolean isFeatured;

    private Instant startDate;

    private Instant endDate;

    private Integer priority;

    private Integer width;

    private Integer height;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    @Size(max = 100, message = "CSS class must not exceed 100 characters")
    private String cssClass;

    @Size(max = 500, message = "CSS style must not exceed 500 characters")
    private String cssStyle;

    private String htmlContent;

    @Size(max = 500, message = "Mobile image URL must not exceed 500 characters")
    private String mobileImageUrl;

    @Size(max = 500, message = "Tablet image URL must not exceed 500 characters")
    private String tabletImageUrl;

    private Integer createdBy;
}
