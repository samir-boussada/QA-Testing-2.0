package com.novashop.service;

import com.novashop.dto.CartItemDto;
import com.novashop.dto.CartSummaryDto;
import com.novashop.dto.CheckoutRequest;
import com.novashop.model.Order;
import com.novashop.model.OrderItem;
import com.novashop.model.Product;
import com.novashop.model.User;
import com.novashop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order placeOrder(User user, CartSummaryDto cart, CheckoutRequest checkoutRequest) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        // Validate stock for all items first
        for (CartItemDto item : cart.getItems()) {
            if (!productService.hasSufficientStock(item.getProductId(), item.getQuantity())) {
                throw new IllegalStateException("Item " + item.getProductName() + " does not have sufficient stock");
            }
        }

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(
                orderNumber,
                user,
                cart.getTotal(),
                checkoutRequest.getShippingAddress(),
                checkoutRequest.getContactPhone()
        );

        // Decrement stock and add order items
        for (CartItemDto item : cart.getItems()) {
            Product product = productService.getProductById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product missing: " + item.getProductId()));

            productService.decrementStock(product.getId(), item.getQuantity());

            OrderItem orderItem = new OrderItem(product, item.getQuantity(), item.getPrice());
            order.addItem(orderItem);
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
