package com.novashop.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novashop.dto.CheckoutRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Add item to cart, apply promo code, and checkout")
    @WithMockUser(username = "customer@novashop.com", authorities = {"ROLE_CUSTOMER"})
    void testEndToEndCartAndCheckout() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // 1. Add Product 1 to Cart
        mockMvc.perform(post("/api/cart/add")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("productId", 1, "quantity", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").exists())
                .andExpect(jsonPath("$.subtotal").exists());

        // 2. Apply Promo Code SAVE10
        mockMvc.perform(post("/api/cart/promo")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("promoCode", "SAVE10"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").isNotEmpty());

        // 3. Checkout with shipping details
        CheckoutRequest checkout = new CheckoutRequest(
                "123 Quality Assurance Way, Tech City",
                "+1234567890",
                "SAVE10"
        );

        mockMvc.perform(post("/api/orders/checkout")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").isNotEmpty())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // 4. Cart should now be empty after checkout
        mockMvc.perform(get("/api/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @DisplayName("Security: Regular customer cannot access Admin endpoints")
    @WithMockUser(username = "customer@novashop.com", authorities = {"ROLE_CUSTOMER"})
    void testAdminEndpoints_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security: Admin can access Admin endpoints")
    @WithMockUser(username = "admin@novashop.com", authorities = {"ROLE_ADMIN"})
    void testAdminEndpoints_AllowedForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk());
    }
}
