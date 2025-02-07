package com.example.demo.model.dto.external;

import java.io.Serializable;

public record RequestProductDto(Long productId, Integer quantity) implements Serializable {

}
