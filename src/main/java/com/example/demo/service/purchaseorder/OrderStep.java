package com.example.demo.service.purchaseorder;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.PurchaseOrderDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface OrderStep {

    void executeStateOperation(PurchaseOrderDto items) throws  OrderTotalComputationException, ProductNotFoundException, QuantityNotAvailableException;

    Optional<OrderStep> next();


}
