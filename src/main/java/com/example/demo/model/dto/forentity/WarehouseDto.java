package com.example.demo.model.dto.forentity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class WarehouseDto {
    private int quantity;
    private ProductDto product;
}
