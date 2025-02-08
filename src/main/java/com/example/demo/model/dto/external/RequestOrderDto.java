package com.example.demo.model.dto.external;

import java.io.Serializable;
import java.util.List;

/**
 * Request order containing list of products requested
 */
public record RequestOrderDto(List<RequestProductDto> items) implements Serializable {

}
