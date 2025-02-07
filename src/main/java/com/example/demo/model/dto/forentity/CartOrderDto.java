package com.example.demo.model.dto.forentity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class CartOrderDto {
    private Long orderId;
    private BigDecimal orderPrice;
    private BigDecimal orderVat;
    private Date orderDate;
    private List<CartOrderProductDto> items;

}
