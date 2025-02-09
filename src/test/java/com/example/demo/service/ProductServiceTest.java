package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.internal.ProductDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.VatRateEntity;
import com.example.demo.repository.IProductRepository;
import com.example.demo.service.implementation.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private IProductRepository productRepository;

    private ProductEntity mockProductEntity() {
        VatRateEntity vatRateEntity = new VatRateEntity();
        vatRateEntity.setPercentage(0.22f);
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setUnitPrice(BigDecimal.TEN);
        mockProduct.setVatRate(vatRateEntity);
        mockProduct.setDescription("mock-product");
        return mockProduct;
    }

    @Test
    void findByProductEntity_productIsAvailable() throws ProductNotFoundException {
        // arrange

        ProductEntity mockProduct = mockProductEntity();
        when(productRepository.findById(11L)).thenReturn(Optional.of(mockProduct));

        // act
        ProductEntity product = productService.findByProductId(11L);

        // assert
        Assertions.assertEquals(BigDecimal.valueOf(12.20f).setScale(2, RoundingMode.HALF_UP), product.getGrossPriceValue());
        Assertions.assertEquals(BigDecimal.valueOf(2.20f).setScale(2, RoundingMode.HALF_UP), product.getVatValue());
        Assertions.assertEquals("mock-product", product.getDescription());
    }

    @Test
    void findByProductEntity_productIdIsNull_throwsIllegalArgumentException() {
        // act
        // assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> productService.findByProductId(null));
        verifyNoInteractions(productRepository);
    }

    @Test
    void findByProductEntity_productIsNotAvailable_throwsProductNotFoundException() {
        // arrange
        when(productRepository.findById(11L)).thenReturn(Optional.empty());
        // act
        // assert
        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.findByProductId(11L));
    }


    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsEnough_removeRequestQuantity() throws ProductNotFoundException, QuantityNotAvailableException {
        // arrange
        ProductEntity mockProduct = mockProductEntity();
        mockProduct.setAvailableQuantity(100);

        ProductEntity productUpdated = mockProductEntity();
        productUpdated.setAvailableQuantity(67);

        when(productRepository.findById(11L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productUpdated);

        // act
        productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);

        // assert
        verify(productRepository, times(1)).save(argThat(entity -> entity.getAvailableQuantity() == 67));
    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_productIdIsNull_throwsIllegalArgumentException() {

        // act
        // assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(null, 33));
        verifyNoInteractions(productRepository);

    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_productIdNotExist_throwsProductNotFoundException() {
        // arrange
        when(productRepository.findById(11L)).thenReturn(Optional.empty());

        // act
        // assert
        Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33));
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(0)).save(any());

    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsNotEnough_throwsQuantityNotAvailableException() {
        // arrange
        ProductEntity product = mockProductEntity();
        product.setAvailableQuantity(10);
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));

        // act
        // assert
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33));
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(0)).save(any());

    }

    @Test
    void ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity_availableQuantityIsEnough_WarehouseDtoHasDataEqualsToEntity() throws ProductNotFoundException, QuantityNotAvailableException {
        // arrange
        ProductEntity mockProduct = mockProductEntity();
        mockProduct.setAvailableQuantity(100);

        ProductEntity productUpdated = mockProductEntity();
        productUpdated.setAvailableQuantity(67);

        when(productRepository.findById(11L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productUpdated);

        // act
        ProductDto productDto = productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(11L, 33);

        // assert
        Assertions.assertEquals(67, productDto.getAvailableQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(12.20f).setScale(2, RoundingMode.HALF_UP), productDto.getGrossPriceValue());
        Assertions.assertEquals(BigDecimal.valueOf(2.20f).setScale(2, RoundingMode.HALF_UP), productDto.getVatValue());

    }
}