package com.novashop.service;

import com.novashop.dto.RegisterRequest;
import com.novashop.model.Role;
import com.novashop.model.User;
import com.novashop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = new User(
                request.getFullName().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_CUSTOMER
        );

        return userRepository.save(user);
    }

    public User createAdminIfMissing(String fullName, String email, String rawPassword) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User admin = new User(fullName, email, passwordEncoder.encode(rawPassword), Role.ROLE_ADMIN);
            return userRepository.save(admin);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}
