package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.IProductRepository;
import org.springframework.stereotype.Service;

/**
 * business logic of products
 */
@Service
public class ProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Find a product entity by product id
     *
     * @param productId id of the product
     * @return product found
     * @throws ProductNotFoundException No product exists with given id
     */
    public ProductEntity findByProductId(Long productId) throws ProductNotFoundException {
        if (productId == null) {
            throw new IllegalArgumentException("Product must not be null required");
        }
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product " + productId + " not found"));
    }

}
