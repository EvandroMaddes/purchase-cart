package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.forentity.WarehouseDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.WarehouseEntity;
import com.example.demo.repository.IWarehouseRepository;
import com.example.demo.service.implementation.WarehouseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {
    @InjectMocks
    private WarehouseService warehouseService;

    @Mock
    private IWarehouseRepository warehouseRepository;

    private ProductEntity mockProductEntity() {
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setPriceValue(BigDecimal.TEN);
        mockProduct.setVatValue(BigDecimal.ONE);
        mockProduct.setDescription("mock-product");
        return mockProduct;
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsEnough_removeRequestQuantity() throws ProductNotFoundException, QuantityNotAvailableException {
        // arrange
        ProductEntity product = mockProductEntity();
        WarehouseEntity warehouse = new WarehouseEntity();
        warehouse.setQuantity(100);
        warehouse.setProduct(product);
        WarehouseEntity warehouseUpdated = new WarehouseEntity();
        warehouseUpdated.setQuantity(67);
        warehouseUpdated.setProduct(product);
        when(warehouseRepository.findByProduct_Id(11L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenReturn(warehouseUpdated);

        //act
        warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);

        //assert
        verify(warehouseRepository, times(1)).save(argThat(entity -> entity.getQuantity() == 67));
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_productIdIsNull_throwsIllegalArgumentException() {

        //act
        try {
            warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(null, 33);
        } catch (Exception e) {
            //assert
            Assertions.assertInstanceOf(IllegalArgumentException.class, e);
            verifyNoInteractions(warehouseRepository);
        }
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_productIdNotExist_throwsProductNotFoundException() {
        // arrange
        when(warehouseRepository.findByProduct_Id(11L)).thenReturn(Optional.empty());

        //act
        try {
            warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);
        } catch (Exception e) {
            //assert
            Assertions.assertInstanceOf(ProductNotFoundException.class, e);
            verify(warehouseRepository, times(1)).findByProduct_Id(anyLong());
            verify(warehouseRepository, times(0)).save(any());
        }
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsNotEnough_throwsQuantityNotAvailableException() {
        // arrange
        ProductEntity product = mockProductEntity();
        WarehouseEntity warehouse = new WarehouseEntity();
        warehouse.setQuantity(10);
        warehouse.setProduct(product);
        when(warehouseRepository.findByProduct_Id(11L)).thenReturn(Optional.of(warehouse));

        //act
        try {
            warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);
        } catch (Exception e) {
            //assert
            Assertions.assertInstanceOf(QuantityNotAvailableException.class, e);
            verify(warehouseRepository, times(1)).findByProduct_Id(anyLong());
            verify(warehouseRepository, times(0)).save(any());
        }
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsEnough_WarehouseDtoHasDataEqualsToEntity() throws ProductNotFoundException, QuantityNotAvailableException {
        // arrange
        ProductEntity product = mockProductEntity();
        WarehouseEntity warehouse = new WarehouseEntity();
        warehouse.setQuantity(100);
        warehouse.setProduct(product);
        WarehouseEntity warehouseUpdated = new WarehouseEntity();
        warehouseUpdated.setQuantity(67);
        warehouseUpdated.setProduct(product);
        when(warehouseRepository.findByProduct_Id(11L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenReturn(warehouseUpdated);

        //act
        WarehouseDto warehouseDto = warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);

        //assert
        Assertions.assertEquals(67, warehouseDto.getQuantity());
        Assertions.assertEquals(BigDecimal.TEN, warehouseDto.getProduct().getPriceValue());
        Assertions.assertEquals(BigDecimal.ONE, warehouseDto.getProduct().getVatValue());

    }

}