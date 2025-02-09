package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Vat entity: vat details
 */
@Entity
@Table(name = "vat_rate")
@Getter
@Setter
public class VatRateEntity extends AbstractPersistable<Long> {
    @Column(name = "description")
    private String description;

    @Column(name = "percentage")
    private Float percentage;
}
