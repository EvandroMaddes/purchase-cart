package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;

/**
 * Product entity: product details
 */
@Entity
@Table(name = "product")
@Getter
@Setter
public class ProductEntity extends AbstractPersistable<Long> {

    @Column(name = "description")
    private String description;

    @Column(name = "price_value")
    private BigDecimal priceValue;

    @Column(name = "vat_value")
    private BigDecimal vatValue;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

}
