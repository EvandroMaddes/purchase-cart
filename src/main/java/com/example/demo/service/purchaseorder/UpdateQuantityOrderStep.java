package com.example.demo.service.purchaseorder;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UpdateQuantityOrderStep implements OrderStep {
    private final WarehouseService warehouseService;
    private final SaveDocumentOrderStep saveDocumentOrderStep;

    public UpdateQuantityOrderStep(WarehouseService warehouseService, SaveDocumentOrderStep saveDocumentOrderStep) {
        this.warehouseService = warehouseService;
        this.saveDocumentOrderStep = saveDocumentOrderStep;
    }

    @Override
    public void executeStateOperation(PurchaseOrderDto order) throws ProductNotFoundException, QuantityNotAvailableException {
        List<PurchaseProductDto> items = order.getItems();
        ifProductQuantitiesAreEnoughThenRemoveRequestedQuantities(items);
    }


    @Override
    public Optional<OrderStep> next() {
        return Optional.of(saveDocumentOrderStep);
    }


    /**
     * Check that each product requested quantity is available
     *
     * @param items list of products
     * @throws ProductNotFoundException      No product exists with given id
     * @throws IllegalArgumentException      at least one product id is null
     * @throws QuantityNotAvailableException available quantity is less than requested quantity
     */
    private void ifProductQuantitiesAreEnoughThenRemoveRequestedQuantities(List<PurchaseProductDto> items) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException {
        for (PurchaseProductDto item : items) {
            warehouseService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(item.getId(), item.getQuantity());
        }
    }


}
