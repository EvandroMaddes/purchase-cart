package com.example.demo.service.integration;

import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.model.entity.CartOrderEntity;
import com.example.demo.model.entity.CartOrderProductEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.VatRateEntity;
import com.example.demo.repository.ICartOrderProductRepository;
import com.example.demo.repository.ICartOrderRepository;
import com.example.demo.repository.IProductRepository;
import com.example.demo.service.implementation.CartOrderService;
import com.example.demo.service.implementation.ProductService;
import com.example.demo.service.implementation.PurchaseOrderOrchestratorService;
import com.example.demo.service.orderstep.SaveNewCartOrderStep;
import com.example.demo.service.orderstep.StartOrderStep;
import com.example.demo.service.orderstep.UpdateQuantityOrderStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        UpdateQuantityOrderStep.class, SaveNewCartOrderStep.class, ProductService.class,
        CartOrderService.class, IProductRepository.class})
class PurchaseOrderOrchestratorServiceIntegrationTest {
    @Autowired
    private PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;

    @MockitoBean
    private ICartOrderRepository cartOrderRepository;
    @MockitoBean
    private ICartOrderProductRepository orderProductRepository;
    @MockitoBean
    private IProductRepository productRepository;


    @Test
    void issueNewOrderWithSteps_enoughQuantityAvailable() throws Exception {
        // arrange
        VatRateEntity vatRate = new VatRateEntity();
        vatRate.setPercentage(0.22f);
        ProductEntity productEntity = new ProductEntity();
        productEntity.setUnitPrice(BigDecimal.TEN);
        productEntity.setVatRate(vatRate);
        productEntity.setAvailableQuantity(5);
        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setUnitPrice(BigDecimal.TEN);
        updatedProductEntity.setVatRate(vatRate);
        updatedProductEntity.setAvailableQuantity(2);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(11L, 3)));

        CartOrderEntity cartOrder = new CartOrderEntity();
        CartOrderProductEntity cartOrderProduct = new CartOrderProductEntity();
        cartOrderProduct.setCartOrder(cartOrder);
        cartOrderProduct.setProduct(productEntity);
        cartOrderProduct.setQuantity(3);
        cartOrder.setPriceValue(updatedProductEntity.getGrossPriceValue().multiply(BigDecimal.valueOf(3)));
        cartOrder.setVatValue(updatedProductEntity.getVatValue().multiply(BigDecimal.valueOf(3)));
        cartOrder.setCartOrderProducts(List.of(cartOrderProduct));
        when(productRepository.save(any())).thenReturn(updatedProductEntity);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
        when(cartOrderRepository.save(any())).thenReturn(cartOrder);

        // act
        ResponseOrderDto responseOrderDto = purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);

        // assert on result
        Assertions.assertEquals(BigDecimal.valueOf(36.60f).setScale(2, RoundingMode.HALF_UP), responseOrderDto.getOrderPrice());
        Assertions.assertEquals(BigDecimal.valueOf(6.60f).setScale(2, RoundingMode.HALF_UP), responseOrderDto.getOrderVat());
        Assertions.assertEquals(1, responseOrderDto.getItems().size());
        Assertions.assertEquals(3, responseOrderDto.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(12.20f).setScale(2, RoundingMode.HALF_UP), responseOrderDto.getItems().getFirst().getPrice());
        Assertions.assertEquals(BigDecimal.valueOf(2.20f).setScale(2, RoundingMode.HALF_UP), responseOrderDto.getItems().getFirst().getVat());
    }

    @Test
    void issueNewOrderWithSteps_notEnoughQuantityAvailable() {
        // arrange
        when(productRepository.findById(anyLong())).thenThrow(new QuantityNotAvailableException("Not enough quantity"));
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(1L, 6)));

        // act
        // assert
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto));

    }

}