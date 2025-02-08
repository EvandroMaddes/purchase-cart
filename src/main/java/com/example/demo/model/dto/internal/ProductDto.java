package com.example.demo.model.dto.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String description;
    private BigDecimal priceValue;
    private BigDecimal vatValue;
}
