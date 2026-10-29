package com.novashop.config;

import com.novashop.model.Product;
import com.novashop.model.Role;
import com.novashop.model.User;
import com.novashop.repository.ProductRepository;
import com.novashop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                  ProductRepository productRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin User
            if (userRepository.findByEmail("admin@novashop.com").isEmpty()) {
                userRepository.save(new User(
                        "Store Admin",
                        "admin@novashop.com",
                        passwordEncoder.encode("AdminPass123!"),
                        Role.ROLE_ADMIN
                ));
            }

            // Seed Standard Customer User
            if (userRepository.findByEmail("customer@novashop.com").isEmpty()) {
                userRepository.save(new User(
                        "Jane Doe",
                        "customer@novashop.com",
                        passwordEncoder.encode("CustomerPass123!"),
                        Role.ROLE_CUSTOMER
                ));
            }

            // Seed Sample Products if empty
            if (productRepository.count() == 0) {
                productRepository.saveAll(Arrays.asList(
                    new Product(
                        "Nova Wireless Noise-Cancelling Headphones",
                        "Premium over-ear headphones with 40-hour battery life and spatial audio support.",
                        new BigDecimal("199.99"),
                        "Audio",
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                        25
                    ),
                    new Product(
                        "NovaPro Mechanical Gaming Keyboard",
                        "Custom hot-swappable tactile switches, RGB per-key backlighting, and aircraft aluminum frame.",
                        new BigDecimal("129.50"),
                        "Electronics",
                        "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500",
                        40
                    ),
                    new Product(
                        "UltraFit Smart Fitness Watch",
                        "Health tracker with AMOLED display, optical heart sensor, GPS, and 50m water resistance.",
                        new BigDecimal("89.00"),
                        "Wearables",
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
                        15
                    ),
                    new Product(
                        "Studio 4K Ultra-Wide Monitor 34\"",
                        "Curved 144Hz productivity monitor with HDR400, USB-C 90W power delivery, and sRGB 99%.",
                        new BigDecimal("499.00"),
                        "Electronics",
                        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500",
                        8
                    ),
                    new Product(
                        "Nova SoundWave Bluetooth Speaker",
                        "IPX7 waterproof portable speaker with deep bass radiator and 24-hour party playback.",
                        new BigDecimal("59.99"),
                        "Audio",
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500",
                        50
                    ),
                    new Product(
                        "Ergonomic Mesh Executive Chair",
                        "Full lumbar support with 4D armrests, breathable mesh back, and tilt lock mechanism.",
                        new BigDecimal("249.00"),
                        "Furniture",
                        "https://images.unsplash.com/photo-1580481077195-c328a37db71a?w=500",
                        12
                    )
                ));
            }
        };
    }
}
