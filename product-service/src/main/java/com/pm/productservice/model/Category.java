package com.pm.productservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "categories", schema = "category_service")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class Category extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "parent_id", columnDefinition = "BINARY(16)")
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CategoryStatus status;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes;


}