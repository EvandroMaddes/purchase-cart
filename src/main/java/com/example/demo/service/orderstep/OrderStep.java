package com.example.demo.service.orderstep;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.PurchaseOrderDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface OrderStep {
    /**
     * Execute operation defined in each step
     *
     * @param purchaseOrder processing order
     * @throws OrderTotalComputationException error computing cumulative value of the order
     * @throws ProductNotFoundException       at least one product is not found
     * @throws QuantityNotAvailableException  at least one product quantity is below the requested one
     */
    void executeStepOperation(PurchaseOrderDto purchaseOrder) throws OrderTotalComputationException, ProductNotFoundException, QuantityNotAvailableException;

    /**
     * The next order step
     * @return next order step
     */
    Optional<OrderStep> next();


}
