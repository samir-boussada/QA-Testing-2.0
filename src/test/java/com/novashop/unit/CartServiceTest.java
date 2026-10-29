package com.novashop.unit;

import com.novashop.dto.CartSummaryDto;
import com.novashop.model.Product;
import com.novashop.service.CartService;
import com.novashop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private Product sampleProduct;
    private CartSummaryDto cart;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                "Wireless Earbuds",
                "High quality audio",
                new BigDecimal("50.00"),
                "Audio",
                "http://example.com/earbuds.jpg",
                10
        );
        sampleProduct.setId(1L);
        cart = new CartSummaryDto();
    }

    @Test
    @DisplayName("Should successfully add product to cart and compute subtotal")
    void testAddToCart_Success() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        cartService.addToCart(cart, 1L, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getSubtotal()).isEqualByComparingTo("100.00");
        assertThat(cart.getShippingFee()).isEqualByComparingTo("0.00"); // Free shipping over 100
        assertThat(cart.getTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Should apply standard 9.99 shipping fee for carts under 100")
    void testShippingFee_UnderThreshold() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        cartService.addToCart(cart, 1L, 1); // 1 * 50 = 50.00

        assertThat(cart.getSubtotal()).isEqualByComparingTo("50.00");
        assertThat(cart.getShippingFee()).isEqualByComparingTo("9.99");
        assertThat(cart.getTotal()).isEqualByComparingTo("59.99");
    }

    @Test
    @DisplayName("Should throw exception when adding quantity exceeding available stock")
    void testAddToCart_ExceedsStock_ThrowsException() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        assertThatThrownBy(() -> cartService.addToCart(cart, 1L, 15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should apply 10% discount for promo code SAVE10")
    void testApplyPromoCode_Save10() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        cartService.addToCart(cart, 1L, 2); // subtotal = 100.00
        cartService.applyPromoCode(cart, "SAVE10");

        assertThat(cart.getDiscount()).isEqualByComparingTo("10.00");
        assertThat(cart.getTotal()).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("Should throw exception for unknown promo code")
    void testApplyPromoCode_InvalidCode() {
        assertThatThrownBy(() -> cartService.applyPromoCode(cart, "INVALID99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid promo code");
    }

    @Test
    @DisplayName("Should remove item from cart and reset totals")
    void testRemoveFromCart() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        cartService.addToCart(cart, 1L, 2);
        assertThat(cart.getItems()).isNotEmpty();

        cartService.removeFromCart(cart, 1L);
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getSubtotal()).isEqualByComparingTo("0.00");
        assertThat(cart.getTotal()).isEqualByComparingTo("0.00");
    }
}
