package com.example.demo.service.integration;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.ICartOrderRepository;
import com.example.demo.repository.IProductRepository;
import com.example.demo.repository.IVatRateRepository;
import com.example.demo.service.implementation.PurchaseOrderOrchestratorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * integration test used to validate purchase order orchestrator logic.
 * Purchase order flow rollback logic is tested here
 */
@SpringBootTest
class PurchaseOrderOrchestratorServiceRollbackIntegrationTest {


    @Autowired
    private PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;

    @MockitoBean
    private ICartOrderRepository cartOrderRepository;

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IVatRateRepository vatRateRepository;

    @Test
    void contextLoads() {
        assertThat(purchaseOrderOrchestratorService).isNotNull();
    }

    @Test
    void issueNewOrderWithSteps_removeProductQuantity_exceptionWhileSavingDocument_rollbackPreviousQuantity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setUnitPrice(BigDecimal.TEN);
        productEntity.setVatRate(vatRateRepository.findById(1L).get());
        productEntity.setDescription("test-product");
        productEntity.setAvailableQuantity(5);
        ProductEntity saved = productRepository.saveAndFlush(productEntity);
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(new RequestProductDto(saved.getId(), 3)));
        when(cartOrderRepository.save(any())).thenThrow(new IllegalArgumentException("Test rollback on product quantities"));
        // act
        Assertions.assertThrows(IllegalArgumentException.class, () -> purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto));

        // assert on db consistency
        Assertions.assertNotNull(saved.getId());
        Optional<ProductEntity> updatedProduct = productRepository.findById(saved.getId());
        Assertions.assertTrue(updatedProduct.isPresent());
        Assertions.assertEquals(5, updatedProduct.get().getAvailableQuantity());
    }

}
