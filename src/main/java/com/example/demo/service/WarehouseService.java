package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.forentity.ProductDto;
import com.example.demo.model.dto.forentity.WarehouseDto;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.model.entity.WarehouseEntity;
import com.example.demo.repository.IWarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * business logic of warehouse (relation product quantity)
 */
@Slf4j
@Service
public class WarehouseService {
    private final IWarehouseRepository warehouseRepository;

    public WarehouseService(IWarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

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
    public WarehouseDto ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(Long productId, Integer requestedQuantity) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException {
        log.info("updating available quantity of product with id: {}", productId);
        WarehouseEntity warehouse = findWarehouseEntityByProductId(productId);
        isQuantityEnoughOrElseThrowQuantityNotAvailableException(warehouse.getQuantity(), requestedQuantity);
        warehouse.setQuantity(warehouse.getQuantity() - requestedQuantity);
        WarehouseEntity saved = warehouseRepository.save(warehouse);
        return mapWarehouseEntityToWarehouseDto(saved);
    }

    private void isQuantityEnoughOrElseThrowQuantityNotAvailableException(int quantityAvailable, int requestedQuantity) throws QuantityNotAvailableException {
        if (quantityAvailable >= requestedQuantity)
            return;
        throw new QuantityNotAvailableException("Quantity not available");
    }

    private WarehouseEntity findWarehouseEntityByProductId(Long productId) throws IllegalArgumentException, ProductNotFoundException {
        if (productId == null) {
            throw new IllegalArgumentException("Product must not be null required");
        }
        return warehouseRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product id: " + productId + " not found"));
    }


    private WarehouseDto mapWarehouseEntityToWarehouseDto(WarehouseEntity warehouseEntity) {
        return WarehouseDto.builder()
                .quantity(warehouseEntity.getQuantity())
                .product(mapProductEntityToProductDto(warehouseEntity.getProduct())).build();
    }

    private ProductDto mapProductEntityToProductDto(ProductEntity product) {
        return ProductDto.builder()
                .id(product.getId())
                .priceValue(product.getPriceValue())
                .description(product.getDescription())
                .vatValue(product.getVatValue())
                .build();
    }
}
