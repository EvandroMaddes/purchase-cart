package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.IProductRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

/**
 * business logic of product
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
     * @return entity
     * @throws ProductNotFoundException No product exists with given id
     * @throws BadRequestException      if productId is null
     */
    public ProductEntity findByProductId(Long productId) throws ProductNotFoundException, BadRequestException {
        if (productId == null) {
            throw new BadRequestException("Product must not be null required");
        }
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product " + productId + " not found"));
    }

}
