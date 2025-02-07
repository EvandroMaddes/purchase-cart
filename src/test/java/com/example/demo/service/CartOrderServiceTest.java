package com.example.demo.service;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.forentity.CartOrderDto;
import com.example.demo.model.entity.CartOrderEntity;
import com.example.demo.model.entity.CartOrderProductEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.ICartOrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    private ProductService productService;

    /**
     * product with price 10, vat 1, description "mock-product"
     */
    private ProductEntity mockProductEntity() {
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setPriceValue(BigDecimal.TEN);
        mockProduct.setVatValue(BigDecimal.ONE);
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
        cartOrder.setPriceValue(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)));
        cartOrder.setVatValue(BigDecimal.ONE.multiply(BigDecimal.valueOf(2)));
        cartOrder.setCartOrderProducts(List.of(cartOrderProduct));
        when(cartOrderRepository.save(any(CartOrderEntity.class))).thenReturn(cartOrder);
        when(productService.findByProductId(11L)).thenReturn(product);
        PurchaseProductDto purchaseProduct = PurchaseProductDto.builder().id(11L).quantity(2).build();

        // act
        CartOrderDto savedCartOrder = cartOrderService.saveNewCartOrder(List.of(purchaseProduct));

        // assert
        Assertions.assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)), savedCartOrder.getOrderPrice());
        Assertions.assertEquals(BigDecimal.ONE.multiply(BigDecimal.valueOf(2)), savedCartOrder.getOrderVat());
        Assertions.assertEquals(1, savedCartOrder.getItems().size());
        Assertions.assertEquals(1, savedCartOrder.getItems().size());
        Assertions.assertEquals(2, savedCartOrder.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.TEN, savedCartOrder.getItems().getFirst().getProduct().getPriceValue());
        Assertions.assertEquals(BigDecimal.ONE, savedCartOrder.getItems().getFirst().getProduct().getVatValue());
    }

    @Test
    void saveNewCartOrder_productPriceIsNull_throwsOrderTotalComputationException() throws IllegalArgumentException, ProductNotFoundException {
        // arrange
        ProductEntity product = mockProductEntity();
        product.setPriceValue(BigDecimal.valueOf(0));
        CartOrderEntity cartOrder = new CartOrderEntity();
        CartOrderProductEntity cartOrderProduct = new CartOrderProductEntity();
        cartOrderProduct.setCartOrder(cartOrder);
        cartOrderProduct.setProduct(product);
        cartOrder.setPriceValue(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)));
        cartOrder.setVatValue(BigDecimal.ONE.multiply(BigDecimal.valueOf(2)));
        cartOrder.setCartOrderProducts(List.of(cartOrderProduct));
        when(cartOrderRepository.save(any(CartOrderEntity.class))).thenReturn(cartOrder);
        when(productService.findByProductId(11L)).thenReturn(product);
        PurchaseProductDto purchaseProduct = PurchaseProductDto.builder().id(11L).quantity(2).build();

        // act
        Assertions.assertThrows(OrderTotalComputationException.class, () -> cartOrderService.saveNewCartOrder(List.of(purchaseProduct)));

    }
}