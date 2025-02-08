package com.example.demo.model.dto.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CartOrderProductDto {
    private int quantity;
    private ProductDto product;
    private CartOrderDto order;
}
