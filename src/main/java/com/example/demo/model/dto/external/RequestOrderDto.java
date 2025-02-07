package com.example.demo.model.dto.external;

import java.io.Serializable;
import java.util.List;

public record RequestOrderDto(List<RequestProductDto> items) implements Serializable {

}
