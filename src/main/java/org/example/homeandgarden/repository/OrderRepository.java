package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
