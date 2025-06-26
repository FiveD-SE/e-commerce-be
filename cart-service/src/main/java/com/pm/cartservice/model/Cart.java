package com.pm.cartservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"cartItems"})
@Data
@SuperBuilder
public final class Cart extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", unique = true, nullable = false, updatable = false)
    private Integer cartId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CartStatus status;

    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double totalAmount;

    @Column(name = "item_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer itemCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CartItem> cartItems;
} 