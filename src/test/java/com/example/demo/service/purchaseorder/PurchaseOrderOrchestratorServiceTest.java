package com.example.demo.service.purchaseorder;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.WarehouseEntity;
import com.example.demo.repository.IWarehouseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class PurchaseOrderOrchestratorServiceTest {
    @Autowired
    private PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;
    @Autowired
    private IWarehouseRepository iWarehouseRepository;


    @Test
    void issueNewOrderWithSteps_enoughQuantityAvailable() throws Exception {
        // arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setPriceValue(BigDecimal.TEN);
        productEntity.setVatValue(BigDecimal.ONE);
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setQuantity(5);
        warehouseEntity.setProduct(productEntity);
        WarehouseEntity saved = iWarehouseRepository.saveAndFlush(warehouseEntity);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(saved.getId(), 3)));

        // act
        ResponseOrderDto responseOrderDto = purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);

        // assert on result
        Assertions.assertNotNull(responseOrderDto.getOrderId());
        Assertions.assertEquals(BigDecimal.TEN.intValueExact() * 3, responseOrderDto.getOrderPrice().intValueExact());
        Assertions.assertEquals(BigDecimal.ONE.intValueExact() * 3, responseOrderDto.getOrderVat().intValueExact());
        Assertions.assertEquals(1, responseOrderDto.getItems().size());
        Assertions.assertEquals(3, responseOrderDto.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.TEN.intValueExact(), responseOrderDto.getItems().getFirst().getPrice().intValueExact());
        Assertions.assertEquals(BigDecimal.ONE.intValueExact(), responseOrderDto.getItems().getFirst().getVat().intValueExact());
        // assert on db consistency
        Assertions.assertNotNull(saved.getId());
        Optional<WarehouseEntity> updatedProduct = iWarehouseRepository.findById(saved.getId());
        Assertions.assertTrue(updatedProduct.isPresent());
        Assertions.assertEquals(2, updatedProduct.get().getQuantity());
    }

    @Test
    void issueNewOrderWithSteps_notEnoughQuantityAvailable() throws Exception {
        // arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setPriceValue(BigDecimal.TEN);
        productEntity.setVatValue(BigDecimal.ONE);
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setQuantity(5);
        warehouseEntity.setProduct(productEntity);
        WarehouseEntity saved = iWarehouseRepository.saveAndFlush(warehouseEntity);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(saved.getId(), 6)));
        //when(orderRepository.save(any())).thenCallRealMethod();

        // act
        try {
            purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);
        } catch (Exception e) {
            Assertions.assertEquals("Quantity not available", e.getMessage());
        }

        // assert on db consistency
        Optional<WarehouseEntity> updatedProduct = iWarehouseRepository.findById(saved.getId());
        Assertions.assertTrue(updatedProduct.isPresent());
        Assertions.assertEquals(5, updatedProduct.get().getQuantity());
    }


    @Test
    void issueNewOrderWithSteps_errorWhileSavingNewOrderOnDb_rollbackQuantityAvailable() throws Exception {
        // arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setPriceValue(BigDecimal.TEN);
        productEntity.setVatValue(BigDecimal.ONE);
        productEntity.setDescription("test-product");
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setQuantity(5);
        warehouseEntity.setProduct(productEntity);
        WarehouseEntity saved = iWarehouseRepository.saveAndFlush(warehouseEntity);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(saved.getId(), 3)));

        // act
        try {
            purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);
        } catch (Exception e) {
            // assert on db consistency
            Optional<WarehouseEntity> updatedProduct = iWarehouseRepository.findById(saved.getId());
            Assertions.assertTrue(updatedProduct.isPresent());
            Assertions.assertEquals(5, updatedProduct.get().getQuantity());


        }
    }

    @Test
    void testScriptExecuted() {
        Optional<WarehouseEntity> updatedProduct = iWarehouseRepository.findById(13L);
        updatedProduct.get().getProduct();
    }
}