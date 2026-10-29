package com.novashop.service;

import com.novashop.dto.CartItemDto;
import com.novashop.dto.CartSummaryDto;
import com.novashop.model.Product;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CartService {

    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public CartSummaryDto addToCart(CartSummaryDto cart, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock. Only " + product.getStockQuantity() + " items available.");
        }

        Optional<CartItemDto> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalStateException("Cannot add more. Reached available stock limit (" + product.getStockQuantity() + ").");
            }
            existingItem.get().setQuantity(newQuantity);
        } else {
            cart.getItems().add(new CartItemDto(product, quantity));
        }

        cart.recalculate();
        return cart;
    }

    public CartSummaryDto updateQuantity(CartSummaryDto cart, Long productId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeFromCart(cart, productId);
        }

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (product.getStockQuantity() < newQuantity) {
            throw new IllegalStateException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ")");
        }

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));

        cart.recalculate();
        return cart;
    }

    public CartSummaryDto removeFromCart(CartSummaryDto cart, Long productId) {
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.recalculate();
        return cart;
    }

    public CartSummaryDto applyPromoCode(CartSummaryDto cart, String promoCode) {
        if (promoCode == null || promoCode.trim().isEmpty()) {
            cart.setDiscount(BigDecimal.ZERO);
            return cart;
        }

        String code = promoCode.trim().toUpperCase();
        if ("SAVE10".equals(code)) {
            BigDecimal discount = cart.getSubtotal().multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
            cart.setDiscount(discount);
        } else if ("NOVA20".equals(code)) {
            BigDecimal discount = cart.getSubtotal().multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);
            cart.setDiscount(discount);
        } else {
            throw new IllegalArgumentException("Invalid promo code: " + promoCode);
        }

        return cart;
    }

    public void clearCart(CartSummaryDto cart) {
        cart.getItems().clear();
        cart.setDiscount(BigDecimal.ZERO);
        cart.recalculate();
    }
}
