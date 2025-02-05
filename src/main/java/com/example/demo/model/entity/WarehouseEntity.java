package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
public class WarehouseEntity extends AbstractPersistable<Long> {
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // foreign key on product.id column
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductEntity product;
}
