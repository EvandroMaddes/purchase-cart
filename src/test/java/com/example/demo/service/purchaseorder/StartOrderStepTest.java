package com.example.demo.service.purchaseorder;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StartOrderStepTest {
    @InjectMocks
    private StartOrderStep startOrderStep;

    @Test
    void executeStateOperation_orderHasNotEmptyProductList_allQuantityGraterThenZero() throws OrderTotalComputationException {
        // arrange
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(List.of(PurchaseProductDto.builder().id(11L).quantity(2).build()))
                .build();

        // act
        startOrderStep.executeStateOperation(order);

        // assert
        Assertions.assertEquals(1, order.getItems().size());
        Assertions.assertEquals(11, order.getItems().getFirst().getId());
        Assertions.assertEquals(2, order.getItems().getFirst().getQuantity());

    }

    @Test
    void executeStateOperation_orderHasEmptyProductList_throwsIllegalArgumentException() {
        // arrange
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(Collections.emptyList())
                .build();

        // act
        // assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> startOrderStep.executeStateOperation(order));
    }


    @Test
    void executeStateOperation_orderHasNotEmptyProductList_allQuantityNotGraterThenZero() {
        // arrange
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(List.of(PurchaseProductDto.builder().id(11L).quantity(-1).build()))
                .build();

        // act
        // assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> startOrderStep.executeStateOperation(order));
    }

    @Test
    void executeStateOperation_orderHasNotEmptyProductList_allQuantityGraterThenZero_mergeQuantityOfSameProductId() throws OrderTotalComputationException {
        // arrange
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(List.of(
                        PurchaseProductDto.builder().id(11L).quantity(3).build(),
                        PurchaseProductDto.builder().id(11L).quantity(4).build()))
                .build();

        // act
        startOrderStep.executeStateOperation(order);
        // assert
        Assertions.assertEquals(1, order.getItems().size());
        Assertions.assertEquals(11, order.getItems().getFirst().getId());
        Assertions.assertEquals(7, order.getItems().getFirst().getQuantity());
    }

    @Test
    void next_UpdateQuantityOrderStep() {
        Optional<OrderStep> next = startOrderStep.next();
        Assertions.assertInstanceOf(UpdateQuantityOrderStep.class, next.orElseGet(Assertions::fail));
    }
}