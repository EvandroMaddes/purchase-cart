package com.example.demo.service.purchaseorder.integration;

import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.model.entity.CartOrderEntity;
import com.example.demo.model.entity.CartOrderProductEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.WarehouseEntity;
import com.example.demo.repository.ICartOrderProductRepository;
import com.example.demo.repository.ICartOrderRepository;
import com.example.demo.repository.IProductRepository;
import com.example.demo.repository.IWarehouseRepository;
import com.example.demo.service.implementation.CartOrderService;
import com.example.demo.service.implementation.ProductService;
import com.example.demo.service.implementation.WarehouseService;
import com.example.demo.service.purchaseorder.PurchaseOrderOrchestratorService;
import com.example.demo.service.purchaseorder.SaveNewCartOrderStep;
import com.example.demo.service.purchaseorder.StartOrderStep;
import com.example.demo.service.purchaseorder.UpdateQuantityOrderStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Integration test used to validate purchase order orchestrator logic.
 * Purchase order flow is tested here
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PurchaseOrderOrchestratorService.class, StartOrderStep.class,
        UpdateQuantityOrderStep.class, SaveNewCartOrderStep.class, ProductService.class, WarehouseService.class,
        CartOrderService.class, IProductRepository.class})
class PurchaseOrderOrchestratorServiceIntegrationTest {
    @Autowired
    private PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;

    @MockitoBean
    private IWarehouseRepository warehouseRepository;

    @MockitoBean
    private ICartOrderRepository cartOrderRepository;
    @MockitoBean
    private ICartOrderProductRepository orderProductRepository;
    @MockitoBean
    private IProductRepository productRepository;


    @Test
    void issueNewOrderWithSteps_enoughQuantityAvailable() throws Exception {
        // arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setPriceValue(BigDecimal.TEN);
        productEntity.setVatValue(BigDecimal.ONE);
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setQuantity(5);
        warehouseEntity.setProduct(productEntity);
        WarehouseEntity updatedWarehouseEntity = new WarehouseEntity();
        updatedWarehouseEntity.setQuantity(2);
        updatedWarehouseEntity.setProduct(productEntity);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(11L, 3)));

        CartOrderEntity cartOrder = new CartOrderEntity();
        CartOrderProductEntity cartOrderProduct = new CartOrderProductEntity();
        cartOrderProduct.setCartOrder(cartOrder);
        cartOrderProduct.setProduct(productEntity);
        cartOrderProduct.setQuantity(3);
        cartOrder.setPriceValue(BigDecimal.TEN.multiply(BigDecimal.valueOf(3)));
        cartOrder.setVatValue(BigDecimal.ONE.multiply(BigDecimal.valueOf(3)));
        cartOrder.setCartOrderProducts(List.of(cartOrderProduct));
        when(warehouseRepository.findByProduct_Id(anyLong())).thenReturn(Optional.of(warehouseEntity));
        when(warehouseRepository.save(any())).thenReturn(updatedWarehouseEntity);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
        when(cartOrderRepository.save(any())).thenReturn(cartOrder);

        // act
        ResponseOrderDto responseOrderDto = purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);

        // assert on result
        Assertions.assertEquals(BigDecimal.TEN.intValueExact() * 3, responseOrderDto.getOrderPrice().intValueExact());
        Assertions.assertEquals(BigDecimal.ONE.intValueExact() * 3, responseOrderDto.getOrderVat().intValueExact());
        Assertions.assertEquals(1, responseOrderDto.getItems().size());
        Assertions.assertEquals(3, responseOrderDto.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.TEN.intValueExact(), responseOrderDto.getItems().getFirst().getPrice().intValueExact());
        Assertions.assertEquals(BigDecimal.ONE.intValueExact(), responseOrderDto.getItems().getFirst().getVat().intValueExact());
    }

    @Test
    void issueNewOrderWithSteps_notEnoughQuantityAvailable() {
        // arrange
        when(warehouseRepository.findByProduct_Id(anyLong())).thenThrow(new QuantityNotAvailableException("Not enough quantity"));
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(1L, 6)));

        // act
        // assert
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto));

    }

}