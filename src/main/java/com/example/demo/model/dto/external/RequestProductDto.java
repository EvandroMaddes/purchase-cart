package com.example.demo.model.dto.external;

import java.io.Serializable;

/**
 * Requested product identified by product id and quantity requested
 */
public record RequestProductDto(Long productId, Integer quantity) implements Serializable {

}
