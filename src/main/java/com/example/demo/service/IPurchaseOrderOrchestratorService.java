package com.example.demo.service;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import org.springframework.transaction.annotation.Transactional;

public interface IPurchaseOrderOrchestratorService {
    /**
     * Process new order request.
     * It executes operations define inside each order step (class implementing orderStep interface)
     * Transactional annotation is needed to guarantee consistency on db data: it ensures that in case of any exception all operations are rolled back.
     * If services will be divided into microservices then an event-driven architecture would be better.
     *
     * @param requestOrderDto order request data
     * @return order response
     * @throws IllegalArgumentException       at least one product has invalid data
     * @throws OrderTotalComputationException error while computing order data
     * @throws ProductNotFoundException       at least one product not found
     */
    @Transactional(rollbackFor = Exception.class)
    ResponseOrderDto issueNewOrderWithSteps(RequestOrderDto requestOrderDto) throws IllegalArgumentException, OrderTotalComputationException, ProductNotFoundException;
}
