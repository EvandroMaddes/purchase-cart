package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Order entity: it has order data and a reference to all products of the order
 */
@Entity
@Table(name = "cart_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderEntity extends AbstractPersistable<Long> {
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Column(name = "price_value")
    private BigDecimal priceValue;

    @Column(name = "vat_value")
    private BigDecimal vatValue;

    // foreign key on cart_order_product.id column
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "cartOrder")
    List<CartOrderProductEntity> cartOrderProducts;
}
