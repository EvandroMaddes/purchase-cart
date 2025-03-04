package com.example.demo.service.implementation;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.model.dto.external.ResponseProductDto;
import com.example.demo.service.IPurchaseOrderOrchestratorService;
import com.example.demo.service.orderstep.OrderStep;
import com.example.demo.service.orderstep.StartOrderStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PurchaseOrderOrchestratorService implements IPurchaseOrderOrchestratorService {
    private final OrderStep orderStep;

    public PurchaseOrderOrchestratorService(StartOrderStep startOrderStep) {
        this.orderStep = startOrderStep;
    }

    /**
     * Process new order request.
     * It executes operations define inside each order step (class implementing orderStep interface)
     * Transactional annotation is needed to guarantee consistency on db data. It ensures that in case of any exception all operations are rolled back.
     * If services will be divided into microservices then an event-driven architecture would be better.
     *
     * @param requestOrderDto order request data
     * @return order response
     * @throws IllegalArgumentException       at least one product has invalid data
     * @throws OrderTotalComputationException error while computing order data
     * @throws ProductNotFoundException       at least one product not found
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseOrderDto issueNewOrderWithSteps(RequestOrderDto requestOrderDto) throws IllegalArgumentException, OrderTotalComputationException, ProductNotFoundException {
        PurchaseOrderDto order = mapRequestOrderDtoToPurchaseOrderDto(requestOrderDto);
        executePurchaseOrderSteps(order);
        return mapPuchaseOrderDtoToResponseOrderDto(order);
    }

    /**
     * Loop over every order step and execute operation all step operations
     *
     * @param order order request data
     * @throws IllegalArgumentException       at least one product has invalid data
     * @throws OrderTotalComputationException error while computing order data
     * @throws ProductNotFoundException       at least one product not found
     */
    private void executePurchaseOrderSteps(PurchaseOrderDto order) throws OrderTotalComputationException, ProductNotFoundException, IllegalArgumentException {
        Optional<OrderStep> step = Optional.of(orderStep);
        try {
            while (step.isPresent()) {
                // execute
                log.info("step: {}", getOrderStepName(step));
                step.get().executeStepOperation(order);
                // go to next state
                step = step.get().next();
            }
        } catch (Exception e) {
            log.error("step: {} failed for exception: {}",
                    getOrderStepName(step),
                    e.getMessage());
            throw e;
        }
    }

    /**
     * If step is present the return its name otherwise return not available name stirng
     * @param step order step required name
     * @return name of the order step
     */
    private String getOrderStepName(Optional<OrderStep> step) {
        return step.isPresent() ? step.get().name() : "order step name not available";
    }

    /**
     * Mapper: from request order dto to purchase order dto
     */
    private PurchaseOrderDto mapRequestOrderDtoToPurchaseOrderDto(RequestOrderDto requestOrderDto) {
        List<PurchaseProductDto> productList = requestOrderDto.items().stream()
                .map(this::mapRequestProductDtoToPurchaseProductDto)
                .toList();
        return PurchaseOrderDto.builder()
                .items(productList)
                .build();
    }


    /**
     * Mapper: from request product dto to purchase product dto
     */
    private PurchaseProductDto mapRequestProductDtoToPurchaseProductDto(RequestProductDto item) {
        return PurchaseProductDto.builder()
                .id(item.productId())
                .quantity(item.quantity()).build();
    }

    /**
     * Mapper: from purchase order dto to response order dto
     */
    private ResponseOrderDto mapPuchaseOrderDtoToResponseOrderDto(PurchaseOrderDto order) {
        return ResponseOrderDto.builder()
                .orderId(order.getOrderId())
                .orderPrice(order.getOrderPrice())
                .orderVat(order.getOrderVat())
                .items(mapPurchaseProductDtoListToResponseProductDto(order.getItems())).build();
    }

    /**
     * Mapper: from purchase product dto list to response product dto list
     */
    private List<ResponseProductDto> mapPurchaseProductDtoListToResponseProductDto(List<PurchaseProductDto> items) {
        return items.stream()
                .map(this::mapPurchaseProductDtoToResponseProductDto)
                .toList();

    }

    /**
     * Mapper: from purchase product dto to response product dto
     */
    private ResponseProductDto mapPurchaseProductDtoToResponseProductDto(PurchaseProductDto purchaseProduct) {
        return ResponseProductDto.builder()
                .id(purchaseProduct.getId())
                .quantity(purchaseProduct.getQuantity())
                .price(purchaseProduct.getPrice())
                .vat(purchaseProduct.getVat())
                .build();

    }

}
