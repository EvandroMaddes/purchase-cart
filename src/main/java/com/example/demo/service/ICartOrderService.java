package com.example.demo.service;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.forentity.CartOrderDto;

import java.util.List;

public interface ICartOrderService {
    /**
     * Map from purchase product dto list to cart order entity.
     * Save new cart order entity.
     * Map from cart order entity to cart order dto
     *
     * @param productList list of purchased product
     * @return order details
     * @throws OrderTotalComputationException result of order data computation is an empty optional
     * @throws ProductNotFoundException       at least one product id does not exist
     */
    CartOrderDto saveNewCartOrder(List<PurchaseProductDto> productList) throws OrderTotalComputationException, ProductNotFoundException;
}
