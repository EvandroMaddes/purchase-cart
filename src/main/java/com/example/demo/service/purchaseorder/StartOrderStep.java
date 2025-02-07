package com.example.demo.service.purchaseorder;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StartOrderStep implements OrderStep {

    private final UpdateQuantityOrderStep updateQuantityOrderStep;

    public StartOrderStep(UpdateQuantityOrderStep updateQuantityOrderStep) {
        this.updateQuantityOrderStep = updateQuantityOrderStep;
    }

    @Override
    public void executeStepOperation(PurchaseOrderDto order) throws OrderTotalComputationException {
        // validate input
        checkNotEmptyProductOrderList(order.getItems());
        checkAllQuantityAreGreaterThanZero(order.getItems());
        mergeProductRequestWithSameProductId(order);
    }

    /**
     * Check list of requested products is not empty
     *
     * @param items list of products
     * @throws IllegalArgumentException list of product is empty
     */
    private void checkNotEmptyProductOrderList(List<PurchaseProductDto> items) throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(items)) {
            throw new IllegalArgumentException("Empty products request");
        }
    }

    /**
     * Check all product quantity are grater than zero
     *
     * @param items list of products
     * @throws IllegalArgumentException at least one quantity less than 1
     */
    private void checkAllQuantityAreGreaterThanZero(List<PurchaseProductDto> items) throws IllegalArgumentException {
        for (PurchaseProductDto product : items) {
            productIdIsNotNull(product);
            quantityIsGraterThanZero(product);
        }
    }

    private void quantityIsGraterThanZero(PurchaseProductDto product) {
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new IllegalArgumentException("invalid product quantity");
        }
    }

    private void productIdIsNotNull(PurchaseProductDto product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("invalid product id");
        }
    }

    /**
     * In case the input order has more than one entry for the same product id.
     * Then merge the input quantity of product with the same product id
     *
     * @param order order
     * @throws OrderTotalComputationException computation error while computing the product quantity
     */
    private void mergeProductRequestWithSameProductId(PurchaseOrderDto order) throws OrderTotalComputationException {
        List<PurchaseProductDto> items = order.getItems();
        List<PurchaseProductDto> mergedItems = new ArrayList<>();
        Set<Long> distinctProductIds = items.stream().map(PurchaseProductDto::getId).collect(Collectors.toSet());
        for (Long id : distinctProductIds) {
            Integer totalQuantity = items.stream()
                    .filter(productDto -> productDto.getId().equals(id))
                    .map(PurchaseProductDto::getQuantity)
                    .reduce(Integer::sum)
                    .orElseThrow(() -> new OrderTotalComputationException("error merging product quantity"));
            mergedItems.add(PurchaseProductDto.builder().id(id).quantity(totalQuantity).build());
        }
        order.setItems(mergedItems);
    }


    @Override
    public Optional<OrderStep> next() {
        return Optional.of(updateQuantityOrderStep);
    }

}
