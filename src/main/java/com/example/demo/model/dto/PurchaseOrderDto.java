package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
public class PurchaseOrderDto {
    private Long orderId;
    private BigDecimal orderPrice;
    private BigDecimal orderVat;
    private List<PurchaseProductDto> items;
}
