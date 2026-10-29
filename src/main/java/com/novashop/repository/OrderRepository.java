package com.novashop.repository;

import com.novashop.model.Order;
import com.novashop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    Optional<Order> findByOrderNumber(String orderNumber);
}
