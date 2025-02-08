package com.example.demo.service.implementation;

import com.example.demo.exception.OrderTotalComputationException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.PurchaseProductDto;
import com.example.demo.model.dto.forentity.CartOrderDto;
import com.example.demo.model.dto.forentity.CartOrderProductDto;
import com.example.demo.model.dto.forentity.ProductDto;
import com.example.demo.model.entity.CartOrderEntity;
import com.example.demo.model.entity.CartOrderProductEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.ICartOrderRepository;
import com.example.demo.service.ICartOrderService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * business logic of orders (relation order products)
 */
@Service
public class CartOrderService implements ICartOrderService {
    private final ICartOrderRepository cartOrderRepository;
    private final ProductService productService;

    public CartOrderService(ICartOrderRepository cartOrderRepository, ProductService productService) {
        this.cartOrderRepository = cartOrderRepository;
        this.productService = productService;
    }

    /**
     * Mapper: from purchase product dto list to cart order entity
     */
    private CartOrderEntity mapToPurchaseOrderDtoListToCartOrderEntity(List<PurchaseProductDto> productList) throws OrderTotalComputationException, ProductNotFoundException {
        CartOrderEntity cartOrderEntity = new CartOrderEntity();
        cartOrderEntity.setCreationDate(Date.from(Instant.now()));
        List<CartOrderProductEntity> orderProducts = mapPuchaseProductDtoListToCartOrderProductList(productList, cartOrderEntity);
        cartOrderEntity.setCartOrderProducts(orderProducts);
        cartOrderEntity.setPriceValue(computeOrderPrice(orderProducts));
        cartOrderEntity.setVatValue(computeOrderVat(orderProducts));
        return cartOrderEntity;
    }

    /**
     * Mapper: from purchase product dto list to cart order product entity list
     */
    private List<CartOrderProductEntity> mapPuchaseProductDtoListToCartOrderProductList(List<PurchaseProductDto> productList, CartOrderEntity cartOrderEntity) throws ProductNotFoundException {
        List<CartOrderProductEntity> orderProductEntities = new ArrayList<>();
        for (PurchaseProductDto product : productList) {
            CartOrderProductEntity cartOrderProductEntity = new CartOrderProductEntity();
            cartOrderProductEntity.setQuantity(product.getQuantityIntValue());
            cartOrderProductEntity.setCartOrder(cartOrderEntity);
            cartOrderProductEntity.setProduct(productService.findByProductId(product.getId()));
            orderProductEntities.add(cartOrderProductEntity);
        }
        return orderProductEntities;
    }

    /**
     * Mapper: from cart order entity to cart order dto
     */
    private CartOrderDto mapCartOrderEntityToOrderDto(CartOrderEntity savedOrder) {
        return CartOrderDto.builder()
                .orderId(savedOrder.getId())
                .orderPrice(savedOrder.getPriceValue())
                .orderVat(savedOrder.getVatValue())
                .items(mapOrderProductEntityToProductDto(savedOrder.getCartOrderProducts()))
                .build();
    }

    /**
     * Mapper: from cart order product entity list to cart order product dto list
     */
    private List<CartOrderProductDto> mapOrderProductEntityToProductDto(List<CartOrderProductEntity> orderProductList) {
        List<CartOrderProductDto> productList = new ArrayList<>();
        for (CartOrderProductEntity orderProduct : orderProductList) {
            CartOrderProductDto product = CartOrderProductDto.builder()
                    .quantity(orderProduct.getQuantity())
                    .product(mapProductEntityToProductDto(orderProduct.getProduct()))
                    .build();
            productList.add(product);
        }
        return productList;
    }

    /**
     * Mapper: from product entity to product dto
     */
    private ProductDto mapProductEntityToProductDto(ProductEntity product) {
        return ProductDto.builder()
                .id(product.getId())
                .priceValue(product.getPriceValue())
                .description(product.getDescription())
                .vatValue(product.getVatValue())
                .build();
    }

    /**
     * Multiply each product vat for product quantity and sum all of result
     *
     * @param cartOrderProductList list of order product
     * @return total order vat
     * @throws OrderTotalComputationException computation result is an empty optional
     */
    private BigDecimal computeOrderVat(List<CartOrderProductEntity> cartOrderProductList) throws OrderTotalComputationException {
        Optional<BigDecimal> orderVat = cartOrderProductList.stream()
                .map(cartOrder -> multiplyByQuantity(cartOrder.getProduct().getVatValue(), cartOrder))
                .reduce(BigDecimal::add);
        return orderVat.orElseThrow(() -> new OrderTotalComputationException("Error computing vat price"));
    }

    /**
     * Multiply each product vat for product quantity and sum all of result
     *
     * @param cartOrderProductList list of order product
     * @return total order price
     * @throws OrderTotalComputationException computation result is an empty optional
     */
    private BigDecimal computeOrderPrice(List<CartOrderProductEntity> cartOrderProductList) throws OrderTotalComputationException {
        Optional<BigDecimal> orderPrice = cartOrderProductList.stream()
                .map(orderProduct -> multiplyByQuantity(orderProduct.getProduct().getPriceValue(), orderProduct))
                .reduce(BigDecimal::add);
        return orderPrice.orElseThrow(() -> new OrderTotalComputationException("Error computing order price"));
    }

    /**
     * Multiply input value by product quantity
     *
     * @param value   value to be multiplied by quantity
     * @param product product used to retrieve quantity
     * @return result of multiplication
     */
    private BigDecimal multiplyByQuantity(BigDecimal value, CartOrderProductEntity product) {
        return value.multiply(BigDecimal.valueOf(product.getQuantity()));
    }

    /**
     * Map from purchase product dto list to cart order entity.
     * Save new cart order entity.
     * Map from cart order entity to cart order dto
     *
     * @param productList list of purchased product
     * @return order details
     * @throws OrderTotalComputationException result of order data computation is an empty optional
     * @throws ProductNotFoundException       at least one product id does not exist
     */
    @Override
    public CartOrderDto saveNewCartOrder(List<PurchaseProductDto> productList) throws OrderTotalComputationException, ProductNotFoundException {
        // save order
        CartOrderEntity cartOrderEntity = mapToPurchaseOrderDtoListToCartOrderEntity(productList);
        CartOrderEntity savedCartOrder = cartOrderRepository.save(cartOrderEntity);
        return mapCartOrderEntityToOrderDto(savedCartOrder);

    }
}
