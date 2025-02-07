package com.example.demo.model.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestProductDto {
    private final Long productId;
    private final Integer quantity;

    public RequestProductDto(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("invalid product id");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("invalid product quantity");
        }
        this.productId = productId;
        this.quantity = quantity;
    }
}
