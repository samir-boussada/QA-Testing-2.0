package com.novashop.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novashop.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Register a new user successfully")
    void testRegisterUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("Samir Tester", "samir.integration@example.com", "Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.email").value("samir.integration@example.com"));
    }

    @Test
    @DisplayName("Integration: Reject registration with duplicate email")
    void testRegisterUser_DuplicateEmail_Conflict() throws Exception {
        RegisterRequest request = new RegisterRequest("Existing User", "customer@novashop.com", "Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("An account with this email already exists"));
    }

    @Test
    @DisplayName("Integration: Successful login with valid credentials")
    void testLogin_Success() throws Exception {
        Map<String, String> credentials = Map.of(
                "email", "customer@novashop.com",
                "password", "CustomerPass123!"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.email").value("customer@novashop.com"))
                .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
    }

    @Test
    @DisplayName("Integration: Reject login with invalid password")
    void testLogin_BadCredentials() throws Exception {
        Map<String, String> credentials = Map.of(
                "email", "customer@novashop.com",
                "password", "WrongPassword999!"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }
}
