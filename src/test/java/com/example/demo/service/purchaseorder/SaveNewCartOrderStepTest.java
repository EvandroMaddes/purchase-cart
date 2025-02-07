package com.example.demo.service.purchaseorder;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.forentity.CartOrderDto;
import com.example.demo.model.dto.forentity.CartOrderProductDto;
import com.example.demo.model.dto.forentity.ProductDto;
import com.example.demo.service.CartOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveNewCartOrderStepTest {
    @InjectMocks
    private SaveNewCartOrderStep saveNewCartOrderStep;
    @Mock
    private CartOrderService cartOrderService;

    private PurchaseProductDto findProductById(List<PurchaseProductDto> items, Long id) {
        return items.stream()
                .filter(item -> Objects.equals(item.getId(), id)).findFirst()
                .orElseGet(Assertions::fail);
    }

    @Test
    void executeStateOperation_mapSavedCartOrderToPurchaseOrderDto() throws OrderTotalComputationException, ProductNotFoundException {
        // arrange
        CartOrderDto cartOrderSaved = CartOrderDto.builder()
                .orderId(1212L)
                .orderVat(BigDecimal.ONE.multiply(BigDecimal.valueOf(4)).add(BigDecimal.TWO.multiply(BigDecimal.valueOf(3))))
                .orderPrice(BigDecimal.TWO.multiply(BigDecimal.valueOf(4)).add(BigDecimal.TEN.multiply(BigDecimal.valueOf(3))))
                .items(List.of(
                        CartOrderProductDto.builder()
                                .product(ProductDto.builder().id(11L).priceValue(BigDecimal.TEN).vatValue(BigDecimal.TWO).build())
                                .quantity(3).build(),
                        CartOrderProductDto.builder()
                                .product(ProductDto.builder().id(12L).priceValue(BigDecimal.TWO).vatValue(BigDecimal.ONE).build())
                                .quantity(4).build()
                )).build();
        List<PurchaseProductDto> productList = List.of(
                PurchaseProductDto.builder().id(11L).quantity(3).build(),
                PurchaseProductDto.builder().id(12L).quantity(4).build());
        when(cartOrderService.saveNewCartOrder(productList)).thenReturn(cartOrderSaved);
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(productList)
                .build();
        // act
        saveNewCartOrderStep.executeStateOperation(order);

        // assert
        Assertions.assertEquals(1212L, order.getOrderId());
        Assertions.assertEquals(BigDecimal.valueOf(38), order.getOrderPrice());
        Assertions.assertEquals(BigDecimal.TEN, order.getOrderVat());
        Assertions.assertEquals(2, order.getItems().size());
        Assertions.assertEquals(11L, findProductById(order.getItems(), 11L).getId());
        Assertions.assertEquals(3, findProductById(order.getItems(), 11L).getQuantity());
        Assertions.assertEquals(BigDecimal.TEN, findProductById(order.getItems(), 11L).getPrice());
        Assertions.assertEquals(BigDecimal.TWO, findProductById(order.getItems(), 11L).getVat());
        Assertions.assertEquals(12L, findProductById(order.getItems(), 12L).getId());
        Assertions.assertEquals(4, findProductById(order.getItems(), 12L).getQuantity());
        Assertions.assertEquals(BigDecimal.TWO, findProductById(order.getItems(), 12L).getPrice());
        Assertions.assertEquals(BigDecimal.ONE, findProductById(order.getItems(), 12L).getVat());
    }

    @Test
    void executeStateOperation_ErrorComputingTotalOrderPrice_throwsOrderTotalComputationException() throws OrderTotalComputationException, ProductNotFoundException {
        // arrange
        List<PurchaseProductDto> productList = List.of(
                PurchaseProductDto.builder().id(11L).quantity(3).build(),
                PurchaseProductDto.builder().id(12L).quantity(4).build());
        when(cartOrderService.saveNewCartOrder(productList)).thenThrow(new OrderTotalComputationException("Error computing total order price"));
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(productList)
                .build();
        // act
        Assertions.assertThrows(OrderTotalComputationException.class, () -> saveNewCartOrderStep.executeStateOperation(order));
    }


    @Test
    void next() {
        Optional<OrderStep> next = saveNewCartOrderStep.next();
        Assertions.assertTrue(next.isEmpty());

    }
}