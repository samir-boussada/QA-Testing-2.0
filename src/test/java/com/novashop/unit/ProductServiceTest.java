package com.novashop.unit;

import com.novashop.model.Product;
import com.novashop.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Gaming Mouse", "Precision optical sensor", new BigDecimal("45.00"), "Electronics", "img.jpg", 10);
        product.setId(5L);
    }

    @Test
    @DisplayName("Should successfully decrement stock when stock is sufficient")
    void testDecrementStock_Success() {
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.decrementStock(5L, 3);

        assertThat(product.getStockQuantity()).isEqualTo(7);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when decrementing more than available stock")
    void testDecrementStock_InsufficientStock() {
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.decrementStock(5L, 15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should verify sufficient stock availability correctly")
    void testHasSufficientStock() {
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        assertThat(productService.hasSufficientStock(5L, 5)).isTrue();
        assertThat(productService.hasSufficientStock(5L, 10)).isTrue();
        assertThat(productService.hasSufficientStock(5L, 11)).isFalse();
        assertThat(productService.hasSufficientStock(5L, 0)).isFalse();
    }
}
