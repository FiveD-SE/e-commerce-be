package com.pm.productservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "product_reviews")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class ProductReview extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReviewStatus status;
} 