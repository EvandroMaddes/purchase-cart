package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.internal.ProductDto;
import com.example.demo.model.entity.ProductEntity;

public interface IProductService {
    /**
     * Find a product entity by product id
     *
     * @param productId id of the product
     * @return product found
     * @throws ProductNotFoundException No product exists with given id
     */
    ProductEntity findByProductId(Long productId) throws ProductNotFoundException;


    /**
     * Find product entity by product id.
     * Check if available quantity is greater or equals to requested quantity.
     * Remove from available quantity the requested quantity.
     * Save updated quantity.
     *
     * @param productId         id of the product
     * @param requestedQuantity requested quantity of the product
     * @return product dto with updated quantity
     * @throws ProductNotFoundException      No product exists with given id
     * @throws IllegalArgumentException      at least one product id is null
     * @throws QuantityNotAvailableException available quantity is less than requested quantity
     */
    ProductDto ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(Long productId, Integer requestedQuantity) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException;

}
