package com.example.demo.service.orderstep;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.service.IProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateQuantityOrderStepTest {

    @InjectMocks
    private UpdateQuantityOrderStep updateQuantityOrderStep;

    @Mock
    // add it to context, used by next() call
    private SaveNewCartOrderStep saveNewCartOrderStep;

    @Mock
    private IProductService productService;

    @Test
    void executeStepOperation_availableQuantitiesAreEnough() throws ProductNotFoundException {
        // arrange
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(List.of(
                        PurchaseProductDto.builder().id(11L).quantity(3).build()
                )).build();
        when(productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 3)).thenReturn(null);

        // act
        updateQuantityOrderStep.executeStepOperation(order);

        // assert
        Assertions.assertEquals(1, order.getItems().size());
        Assertions.assertEquals(11, order.getItems().getFirst().getId());
        Assertions.assertEquals(3, order.getItems().getFirst().getQuantity());
    }

    @Test
    void executeStepOperation_availableQuantitiesAreNotEnough_throwsQuantityNotAvailableException() throws ProductNotFoundException {
        PurchaseOrderDto order = PurchaseOrderDto.builder()
                .items(List.of(
                        PurchaseProductDto.builder().id(11L).quantity(3).build()
                )).build();
        when(productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 3)).thenThrow(new QuantityNotAvailableException("quantity is not available"));

        // act
        // assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> updateQuantityOrderStep.executeStepOperation(order));
    }

    @Test
    void next_SaveDocumentOrderStep() {
        Optional<OrderStep> next = updateQuantityOrderStep.next();
        Assertions.assertInstanceOf(SaveNewCartOrderStep.class, next.orElseGet(Assertions::fail));
    }

    @Test
    void name_UpdateQuantityOrderStep() {
        String name = updateQuantityOrderStep.name();
        Assertions.assertEquals("UpdateQuantityOrderStep", name);
    }

}