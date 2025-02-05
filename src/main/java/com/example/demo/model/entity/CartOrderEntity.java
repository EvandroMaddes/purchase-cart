package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cart_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderEntity extends AbstractPersistable<Long> {
    @Column(name = "order_date", nullable = false)
    private Date orderDate;

    @Column(name = "order_price_value")
    private BigDecimal orderPriceValue;

    @Column(name = "order_vat_value")
    private BigDecimal orderVatValue;

    // foreign key on order_product.id column
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_product_id", referencedColumnName = "id")
    List<CartOrderProductEntity> orderProducts;
}
