package com.pm.contentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "banners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "link_text", length = 100)
    private String linkText;

    @Column(name = "position", length = 50)
    private String position; // HEADER, FOOTER, SIDEBAR, MAIN, POPUP

    @Column(name = "type", length = 50)
    @Builder.Default
    private String type = "IMAGE"; // IMAGE, VIDEO, HTML

    @Column(name = "target", length = 20)
    @Builder.Default
    private String target = "_self"; // _self, _blank

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "css_class", length = 100)
    private String cssClass;

    @Column(name = "css_style", length = 500)
    private String cssStyle;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    @Column(name = "tablet_image_url", length = 500)
    private String tabletImageUrl;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Helper methods
    public void incrementClickCount() {
        this.clickCount = this.clickCount + 1;
    }

    public void incrementViewCount() {
        this.viewCount = this.viewCount + 1;
    }

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

    public boolean isImage() {
        return "IMAGE".equals(this.type);
    }

    public boolean isVideo() {
        return "VIDEO".equals(this.type);
    }

    public boolean isHtml() {
        return "HTML".equals(this.type);
    }
}
