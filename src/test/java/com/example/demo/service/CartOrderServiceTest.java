package com.example.demo.service;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.internal.CartOrderDto;
import com.example.demo.model.entity.CartOrderEntity;
import com.example.demo.model.entity.CartOrderProductEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.VatRateEntity;
import com.example.demo.repository.ICartOrderRepository;
import com.example.demo.service.implementation.CartOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartOrderServiceTest {
    @InjectMocks
    private CartOrderService cartOrderService;

    @Mock
    private ICartOrderRepository cartOrderRepository;
    @Mock
    private IProductService productService;

    /**
     * product with price 10, vat 1, description "mock-product"
     */
    private ProductEntity mockProductEntity() {
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setUnitPrice(BigDecimal.TEN);
        VatRateEntity vatRate = new VatRateEntity();
        vatRate.setPercentage(0.22f);
        mockProduct.setVatRate(vatRate);
        mockProduct.setDescription("mock-product");
        return mockProduct;
    }

    @Test
    void saveNewCartOrder_CartOrderDtoHasDataEqualsToEntity() throws IllegalArgumentException, ProductNotFoundException, OrderTotalComputationException {
        // arrange
        ProductEntity product = mockProductEntity();
        CartOrderEntity cartOrder = new CartOrderEntity();
        CartOrderProductEntity cartOrderProduct = new CartOrderProductEntity();
        cartOrderProduct.setCartOrder(cartOrder);
        cartOrderProduct.setProduct(product);
        cartOrderProduct.setQuantity(2);
        cartOrder.setPriceValue(product.getGrossPriceValue().multiply(BigDecimal.TWO));
        cartOrder.setVatValue(product.getVatValue().multiply(BigDecimal.TWO));
        cartOrder.setCartOrderProducts(List.of(cartOrderProduct));
        when(cartOrderRepository.save(any(CartOrderEntity.class))).thenReturn(cartOrder);
        when(productService.findByProductId(11L)).thenReturn(product);
        PurchaseProductDto purchaseProduct = PurchaseProductDto.builder().id(11L).quantity(2).build();

        // act
        CartOrderDto savedCartOrder = cartOrderService.saveNewCartOrder(List.of(purchaseProduct));

        // assert
        Assertions.assertEquals(BigDecimal.valueOf(24.40f).setScale(2, RoundingMode.HALF_UP), savedCartOrder.getOrderPrice());
        Assertions.assertEquals(BigDecimal.valueOf(4.40f).setScale(2, RoundingMode.HALF_UP), savedCartOrder.getOrderVat());
        Assertions.assertEquals(1, savedCartOrder.getItems().size());
        Assertions.assertEquals(1, savedCartOrder.getItems().size());
        Assertions.assertEquals(2, savedCartOrder.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(12.20).setScale(2, RoundingMode.HALF_UP), savedCartOrder.getItems().getFirst().getProduct().getGrossPriceValue());
        Assertions.assertEquals(BigDecimal.valueOf(2.20).setScale(2, RoundingMode.HALF_UP), savedCartOrder.getItems().getFirst().getProduct().getVatValue());
    }
}