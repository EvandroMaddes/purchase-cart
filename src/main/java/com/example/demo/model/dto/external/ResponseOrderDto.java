package com.example.demo.model.dto.external;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class ResponseOrderDto  implements Serializable {
    private Long orderId;
    private BigDecimal orderPrice;
    private BigDecimal orderVat;
    private List<ResponseProductDto> items;
}
