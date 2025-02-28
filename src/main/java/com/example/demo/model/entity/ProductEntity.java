package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    // foreign key on vat_rate.id column
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vat_rate_id", referencedColumnName = "id")
    private VatRateEntity vatRate;

    /**
     * Compute product vat value
     *
     * @return vat value
     */
    public BigDecimal getVatValue() {
        Float vatPercentage = vatRate.getPercentage();
        BigDecimal vatValue = unitPrice.multiply(BigDecimal.valueOf(vatPercentage));
        return setScale2HalfUp(vatValue);
    }

    /**
     * Compute product gross price value
     *
     * @return gross price
     */
    public BigDecimal getGrossPriceValue() {
        BigDecimal grossPrice = unitPrice.add(getVatValue());
        return setScale2HalfUp(grossPrice);
    }

    /**
     * Format unit price before save and update its value on db
     */
    @PrePersist
    @PreUpdate
    public void pricePrecisionConversion() {
        this.unitPrice = setScale2HalfUp(unitPrice);
    }

    /**
     * Set scale 2 and rounding mode half up
     *
     * @param bigDecimal number to be formatted
     * @return formatted number
     */
    private BigDecimal setScale2HalfUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }
}
