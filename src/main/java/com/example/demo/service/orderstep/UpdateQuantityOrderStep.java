package com.example.demo.service.orderstep;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.QuantityNotAvailableException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UpdateQuantityOrderStep implements OrderStep {
    private final IProductService productService;
    private final SaveNewCartOrderStep saveNewCartOrderStep;

    public UpdateQuantityOrderStep(IProductService productService, SaveNewCartOrderStep saveNewCartOrderStep) {
        this.productService = productService;
        this.saveNewCartOrderStep = saveNewCartOrderStep;
    }

    /**
     * If available quantities are enough then remove the requested quantities
     */
    @Override
    public void executeStepOperation(PurchaseOrderDto purchaseOrder) throws ProductNotFoundException, QuantityNotAvailableException {
        List<PurchaseProductDto> items = purchaseOrder.getItems();
        reserveProductsQuantity(items);
    }


    @Override
    public Optional<OrderStep> next() {
        return Optional.of(saveNewCartOrderStep);
    }

    /**
     * The step order name
     */
    @Override
    public String name() {
        return "UpdateQuantityOrderStep";
    }


    /**
     * Check that each product requested quantity is available, and remove it.
     *
     * @param items list of products
     * @throws ProductNotFoundException      No product exists with given id
     * @throws IllegalArgumentException      at least one product id is null
     * @throws QuantityNotAvailableException available quantity is less than requested quantity
     */
    private void reserveProductsQuantity(List<PurchaseProductDto> items) throws IllegalArgumentException, ProductNotFoundException, QuantityNotAvailableException {
        for (PurchaseProductDto item : items) {
            productService.ifQuantityAvailableIsEnoughThenRemoveRequestedQuantity(item.getId(), item.getQuantity());
        }
    }


}
