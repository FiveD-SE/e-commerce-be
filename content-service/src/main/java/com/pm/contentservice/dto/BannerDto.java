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
public class BannerDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String label;
    private String imageUrl;
    private String title;
    private String subtitle;
    private String description;
    private String linkUrl;
    private String linkText;
    private String position;
    private String type;
    private String target;
    private Boolean isActive;
    private Boolean isFeatured;
    private Long clickCount;
    private Long viewCount;
    private Instant startDate;
    private Instant endDate;
    private Integer priority;
    private Integer width;
    private Integer height;
    private String altText;
    private String cssClass;
    private String cssStyle;
    private String htmlContent;
    private String mobileImageUrl;
    private String tabletImageUrl;
    private Integer createdBy;
    private Integer updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    public boolean isActive() {
        if (!Boolean.TRUE.equals(this.isActive)) {
            return false;
        }

        Instant now = Instant.now();
        
        // Check start date
        if (this.startDate != null && now.isBefore(this.startDate)) {
            return false;
        }

        // Check end date
        if (this.endDate != null && now.isAfter(this.endDate)) {
            return false;
        }

        return true;
    }

    public boolean isExpired() {
        return this.endDate != null && Instant.now().isAfter(this.endDate);
    }

    public boolean isScheduled() {
        return this.startDate != null && Instant.now().isBefore(this.startDate);
    }

    public String getStatusDisplay() {
        if (isExpired()) {
            return "Expired";
        } else if (isScheduled()) {
            return "Scheduled";
        } else if (isActive()) {
            return "Active";
        } else {
            return "Inactive";
        }
    }

    public String getTypeDisplay() {
        switch (type != null ? type : "IMAGE") {
            case "IMAGE":
                return "Image Banner";
            case "VIDEO":
                return "Video Banner";
            case "HTML":
                return "HTML Banner";
            default:
                return type;
        }
    }

    public String getPositionDisplay() {
        switch (position != null ? position : "") {
            case "HEADER":
                return "Header";
            case "FOOTER":
                return "Footer";
            case "SIDEBAR":
                return "Sidebar";
            case "MAIN":
                return "Main Content";
            case "POPUP":
                return "Popup";
            default:
                return position;
        }
    }
}
