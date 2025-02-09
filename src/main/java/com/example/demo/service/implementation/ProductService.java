package com.example.demo.service.implementation;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.internal.ProductDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.IProductRepository;
import com.example.demo.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * business logic of products
 */
@Slf4j
@Service
public class ProductService implements IProductService {
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
    @Override
    public ProductEntity findByProductId(Long productId) throws ProductNotFoundException {
        if (productId == null) {
            throw new IllegalArgumentException("Product must not be null required");
        }
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product " + productId + " not found"));

    }


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
    public ProductDto ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(Long productId, Integer requestedQuantity) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException {
        log.info("updating available quantity of product with id: {}", productId);
        ProductEntity product = findByProductId(productId);
        isQuantityEnoughOrElseThrowQuantityNotAvailableException(product.getAvailableQuantity(), requestedQuantity);
        product.setAvailableQuantity(product.getAvailableQuantity() - requestedQuantity);
        ProductEntity saved = productRepository.save(product);
        return mapProductEntityToProductDto(saved);
    }

    private void isQuantityEnoughOrElseThrowQuantityNotAvailableException(int quantityAvailable, int requestedQuantity) throws QuantityNotAvailableException {
        if (quantityAvailable >= requestedQuantity)
            return;
        throw new QuantityNotAvailableException("Quantity not available");
    }


    private ProductDto mapProductEntityToProductDto(ProductEntity product) {
        return ProductDto.builder()
                .id(product.getId())
                .grossPriceValue(product.getGrossPriceValue())
                .description(product.getDescription())
                .vatPriceValue(product.getVatValue())
                .availableQuantity(product.getAvailableQuantity())
                .build();
    }
}
