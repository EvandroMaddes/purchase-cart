package com.example.demo.model.dto.external;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class ResponseProductDto {
    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal vat;
}
