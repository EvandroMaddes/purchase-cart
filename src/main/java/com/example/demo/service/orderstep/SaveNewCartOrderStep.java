package com.example.demo.service.orderstep;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseOrderDto;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.internal.CartOrderDto;
import com.example.demo.model.dto.internal.CartOrderProductDto;
import com.example.demo.model.dto.internal.ProductDto;
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

    /**
     * Save new order and add order details to purchase order object
     */
    @Override
    public void executeStepOperation(PurchaseOrderDto purchaseOrder) throws OrderTotalComputationException, ProductNotFoundException {
        CartOrderDto orderDocument = saveNewCartOrder(purchaseOrder.getItems());
        addCartOrderDataToPurchaseOrderDto(purchaseOrder, orderDocument);
    }

    @Override
    public Optional<OrderStep> next() {
        return Optional.empty();
    }

    /**
     * The step order name
     */
    @Override
    public String name() {
        return "SaveNewCartOrderStep";
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
            ProductDto product = item.getProduct();
            PurchaseProductDto purchaseProduct = PurchaseProductDto.builder()
                    .id(product.getId())
                    .price(product.getGrossPriceValue())
                    .vat(product.getVatPriceValue())
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
