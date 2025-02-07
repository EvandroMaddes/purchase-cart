package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.IProductRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private IProductRepository productRepository;

    @Test
    void findByProductEntity_productIsAvailable() throws BadRequestException, ProductNotFoundException {
        // arrange
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setPriceValue(BigDecimal.TEN);
        mockProduct.setVatValue(BigDecimal.ONE);
        mockProduct.setDescription("mock-product");
        when(productRepository.findById(11L)).thenReturn(Optional.of(mockProduct));

        // act
        ProductEntity product = productService.findByProductId(11L);

        // assert
        Assertions.assertEquals(BigDecimal.TEN, product.getPriceValue());
        Assertions.assertEquals(BigDecimal.ONE, product.getVatValue());
        Assertions.assertEquals("mock-product", product.getDescription());
    }

    @Test
    void findByProductEntity_productIdIsNull_throwsBadRequestException() {
        try {
            // act
            productService.findByProductId(null);
        } catch (Exception e) {
            //assert
            Assertions.assertInstanceOf(BadRequestException.class, e);
        }
    }

    @Test
    void findByProductEntity_productIsNotAvailable_throwsProductNotFoundException() {
        // arrange
        when(productRepository.findById(11L)).thenReturn(Optional.empty());

        try {
            // act
            productService.findByProductId(11L);
        } catch (Exception e) {
            //assert
            Assertions.assertInstanceOf(ProductNotFoundException.class, e);
        }
    }
}