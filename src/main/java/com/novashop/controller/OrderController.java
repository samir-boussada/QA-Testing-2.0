package com.novashop.controller;

import com.novashop.dto.CartSummaryDto;
import com.novashop.dto.CheckoutRequest;
import com.novashop.model.Order;
import com.novashop.model.User;
import com.novashop.service.CartService;
import com.novashop.service.OrderService;
import com.novashop.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final String CART_SESSION_KEY = "NOVASHOP_CART";
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication,
            HttpSession session) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please login to complete checkout"));
        }

        User user = userService.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        CartSummaryDto cart = (CartSummaryDto) session.getAttribute(CART_SESSION_KEY);
        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Your cart is empty. Please add items before checking out."));
        }

        try {
            Order order = orderService.placeOrder(user, cart, request);
            cartService.clearCart(cart);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order placed successfully!");
            response.put("orderNumber", order.getOrderNumber());
            response.put("totalAmount", order.getTotalAmount());
            response.put("status", order.getStatus().name());
            response.put("shippingAddress", order.getShippingAddress());
            response.put("orderDate", order.getOrderDate());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }
}
