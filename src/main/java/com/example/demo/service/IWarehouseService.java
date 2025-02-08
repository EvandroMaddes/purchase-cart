package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.forentity.WarehouseDto;

public interface IWarehouseService {
    /**
     * Find warehouse entity by product id.
     * Check if available quantity is greater or equals to requested quantity.
     * Remove from available quantity the requested quantity.
     * Save updated quantity.
     *
     * @param productId         id of the product
     * @param requestedQuantity requested quantity of the product
     * @return warehouse dto with updated quantity
     * @throws ProductNotFoundException      No product exists with given id
     * @throws IllegalArgumentException      at least one product id is null
     * @throws QuantityNotAvailableException available quantity is less than requested quantity
     */
    WarehouseDto ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(Long productId, Integer requestedQuantity) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException;
}
