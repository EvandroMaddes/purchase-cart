package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
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
}
