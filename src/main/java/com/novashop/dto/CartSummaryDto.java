package com.novashop.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CartSummaryDto {
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    public void recalculate() {
        this.subtotal = items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Free shipping for orders over 100
        if (subtotal.compareTo(BigDecimal.valueOf(100)) >= 0 || subtotal.compareTo(BigDecimal.ZERO) == 0) {
            this.shippingFee = BigDecimal.ZERO;
        } else {
            this.shippingFee = BigDecimal.valueOf(9.99);
        }

        this.total = subtotal.subtract(discount).add(shippingFee).setScale(2, RoundingMode.HALF_UP);
        if (this.total.compareTo(BigDecimal.ZERO) < 0) {
            this.total = BigDecimal.ZERO;
        }
    }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; recalculate(); }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; recalculate(); }

    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getTotal() { return total; }
}
