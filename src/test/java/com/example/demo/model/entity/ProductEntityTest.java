package com.example.demo.model.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

class ProductEntityTest {

    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        VatRateEntity vatRateEntity = new VatRateEntity();
        vatRateEntity.setPercentage(0.22f);
        productEntity = new ProductEntity();
        productEntity.setUnitPrice(BigDecimal.TEN);
        productEntity.setAvailableQuantity(3);
        productEntity.setDescription("test-product-entity");
        productEntity.setVatRate(vatRateEntity);
    }

    @Test
    void getVatValue_computeProductVatValue() {
        // arrange
        BigDecimal vatValue = productEntity.getVatValue();
        // assert
        Assertions.assertEquals(BigDecimal.valueOf(2.20).setScale(2, RoundingMode.HALF_UP), vatValue);
    }

    @Test
    void getGrossPriceValue_computeProductGrossPriceValue() {
        // arrange
        BigDecimal grossPrice = productEntity.getGrossPriceValue();
        // assert
        Assertions.assertEquals(BigDecimal.valueOf(12.20).setScale(2, RoundingMode.HALF_UP), grossPrice);
    }
}