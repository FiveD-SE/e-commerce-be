package com.pm.orderservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"order"})
@Data
@SuperBuilder
public class OrderItem extends AbstractMappedEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", unique = true, nullable = false, updatable = false)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_sku", nullable = false, length = 50)
    private String productSku;

    @Column(name = "unit_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalPrice;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;
} 