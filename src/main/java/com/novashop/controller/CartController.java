package com.novashop.controller;

import com.novashop.dto.CartSummaryDto;
import com.novashop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final String CART_SESSION_KEY = "NOVASHOP_CART";
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private CartSummaryDto getOrCreateCart(HttpSession session) {
        CartSummaryDto cart = (CartSummaryDto) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new CartSummaryDto();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @GetMapping
    public ResponseEntity<CartSummaryDto> getCart(HttpSession session) {
        return ResponseEntity.ok(getOrCreateCart(session));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = payload.containsKey("quantity") ? Integer.parseInt(payload.get("quantity").toString()) : 1;

            CartSummaryDto cart = getOrCreateCart(session);
            cartService.addToCart(cart, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            CartSummaryDto cart = getOrCreateCart(session);
            cartService.updateQuantity(cart, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartSummaryDto> removeItem(@PathVariable Long productId, HttpSession session) {
        CartSummaryDto cart = getOrCreateCart(session);
        cartService.removeFromCart(cart, productId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/promo")
    public ResponseEntity<?> applyPromo(@RequestBody Map<String, String> payload, HttpSession session) {
        try {
            String promoCode = payload.get("promoCode");
            CartSummaryDto cart = getOrCreateCart(session);
            cartService.applyPromoCode(cart, promoCode);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<CartSummaryDto> clearCart(HttpSession session) {
        CartSummaryDto cart = getOrCreateCart(session);
        cartService.clearCart(cart);
        return ResponseEntity.ok(cart);
    }
}
