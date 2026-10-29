package com.novashop.dto;

import com.novashop.model.Product;
import java.math.BigDecimal;

public class CartItemDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private int quantity;

    public CartItemDto() {}

    public CartItemDto(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
