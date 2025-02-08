package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Order product entity: it links an order to its products and store the product quantity
 */
@Entity
@Table(name = "cart_order_product")
@Getter
@Setter
public class CartOrderProductEntity extends AbstractPersistable<Long> {
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // foreign key on product.id column
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductEntity product;

    // foreign key on order.id column
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_order_id", referencedColumnName = "id")
    private CartOrderEntity cartOrder;
}
