package com.pm.productservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_inventory")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}