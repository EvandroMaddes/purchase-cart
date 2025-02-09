package com.example.demo.controller;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.model.dto.external.ResponseProductDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.IProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;


/**
 * end-to-end test used to validate purchase order orchestrator logic.
 * Purchase order flow is tested here
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PurchaseOrderControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IProductRepository productRepository;

    @Test
    void createNewOrder_ok_() {
        // arrange
        String url = "http://localhost:" + port + "/api/v1/order";
        RequestOrderDto requestOrder = new RequestOrderDto(List.of(
                new RequestProductDto(12L, 2)
        ));

        // act
        ResponseEntity<ResponseOrderDto> responseOrderDtoResponseEntity = restTemplate.postForEntity(url, requestOrder, ResponseOrderDto.class);
        // assert
        Assertions.assertTrue(responseOrderDtoResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.OK));
        ResponseOrderDto responseOrder = responseOrderDtoResponseEntity.getBody();
        ProductEntity savedProduct = productRepository.findById(12L).orElseGet((Assertions::fail));
        Assertions.assertNotNull(responseOrder);
        Assertions.assertNotNull(responseOrder.getOrderId());
        Assertions.assertNotNull(responseOrder.getItems());
        ResponseProductDto firstItem = responseOrder.getItems().getFirst();
        Assertions.assertNotNull(firstItem);
        Assertions.assertNotNull(firstItem.getId());
        Assertions.assertEquals(savedProduct.getPriceValue(), firstItem.getPrice());
        Assertions.assertEquals(savedProduct.getVatValue(), firstItem.getVat());
        Assertions.assertEquals(savedProduct.getPriceValue().multiply(BigDecimal.TWO), responseOrder.getOrderPrice());
        Assertions.assertEquals(savedProduct.getVatValue().multiply(BigDecimal.TWO), responseOrder.getOrderVat());

    }

    @Test
    void createNewOrder_productIdNotFound_httpStatusNotFound() {
        String url = "http://localhost:" + port + "/api/v1/order";
        RequestOrderDto requestOrder = new RequestOrderDto(List.of(
                new RequestProductDto(22L, 2)
        ));
        // act
        ResponseEntity<String> responseOrderDtoResponseEntity = restTemplate.postForEntity(url, requestOrder, String.class);
        // assert
        Assertions.assertTrue(responseOrderDtoResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND));
    }

    @Test
    void createNewOrder_quantityNotAvailable_httpStatusBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/order";
        RequestOrderDto requestOrder = new RequestOrderDto(List.of(
                new RequestProductDto(12L, 5)
        ));
        // act
        ResponseEntity<String> responseOrderDtoResponseEntity = restTemplate.postForEntity(url, requestOrder, String.class);
        // assert
        Assertions.assertTrue(responseOrderDtoResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
    }

    @Test
    void createNewOrder_quantityBelowOne_httpStatusBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/order";
        RequestOrderDto requestOrder = new RequestOrderDto(List.of(
                new RequestProductDto(12L, 0)
        ));
        // act
        ResponseEntity<String> responseOrderDtoResponseEntity = restTemplate.postForEntity(url, requestOrder, String.class);
        // assert
        Assertions.assertTrue(responseOrderDtoResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
    }

    @Test
    void createNewOrder_productIdIsNull_httpStatusBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/order";
        RequestOrderDto requestOrder = new RequestOrderDto(List.of(
                new RequestProductDto(null, 2)
        ));
        // act
        ResponseEntity<String> responseOrderDtoResponseEntity = restTemplate.postForEntity(url, requestOrder, String.class);
        // assert
        Assertions.assertTrue(responseOrderDtoResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
    }


}