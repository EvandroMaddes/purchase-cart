package com.example.demo.service.purchaseorder;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.forentity.CartOrderDto;
import com.example.demo.model.dto.forentity.CartOrderProductDto;
import com.example.demo.service.ICartOrderService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SaveNewCartOrderStep implements OrderStep {
    private final ICartOrderService cartOrderService;

    public SaveNewCartOrderStep(ICartOrderService cartOrderService) {
        this.cartOrderService = cartOrderService;
    }

    @Override
    public void executeStepOperation(PurchaseOrderDto order) throws OrderTotalComputationException, ProductNotFoundException {
        CartOrderDto orderDocument = saveNewCartOrder(order.getItems());
        addCartOrderDataToPurchaseOrderDto(order, orderDocument);
    }

    @Override
    public Optional<OrderStep> next() {
        return Optional.empty();
    }


    private void addCartOrderDataToPurchaseOrderDto(PurchaseOrderDto order, CartOrderDto orderSaved) {
        order.setOrderId(orderSaved.getOrderId());
        order.setOrderPrice(orderSaved.getOrderPrice());
        order.setOrderVat(orderSaved.getOrderVat());
        order.setItems(mapCartOrderProductDtoListToPurchaseProductDtoList(orderSaved.getItems()));
    }

    /**
     * Mapper: from cart order product dto list to purchase product dto list
     */
    private List<PurchaseProductDto> mapCartOrderProductDtoListToPurchaseProductDtoList(List<CartOrderProductDto> items) {
        List<PurchaseProductDto> purchaseProductList = new ArrayList<>();
        for (CartOrderProductDto item : items) {
            PurchaseProductDto purchaseProduct = PurchaseProductDto.builder()
                    .id(item.getProduct().getId())
                    .price(item.getProduct().getPriceValue())
                    .vat(item.getProduct().getVatValue())
                    .quantity(item.getQuantity())
                    .build();
            purchaseProductList.add(purchaseProduct);
        }
        return purchaseProductList;
    }

    /**
     * Save new order
     *
     * @param items list of products
     * @return order saved
     * @throws OrderTotalComputationException error while computing total vat, total price
     * @throws ProductNotFoundException       at least one product is not found
     */
    private CartOrderDto saveNewCartOrder(List<PurchaseProductDto> items) throws OrderTotalComputationException, ProductNotFoundException {
        return cartOrderService.saveNewCartOrder(items);
    }
}
