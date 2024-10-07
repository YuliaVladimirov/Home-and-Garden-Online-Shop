package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("DELETE FROM CartItem cartItem " +
            "WHERE cartItem.cartItemId = :id")
    void deleteById(Long id);
}
