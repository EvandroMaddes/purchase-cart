package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class PurchaseProductDto {
    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal vat;

    public int getQuantityIntValue() {
        if (this.quantity == null) return 0;
        {
            return this.quantity;
        }
    }
}
