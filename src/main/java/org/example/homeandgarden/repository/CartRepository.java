package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

}
