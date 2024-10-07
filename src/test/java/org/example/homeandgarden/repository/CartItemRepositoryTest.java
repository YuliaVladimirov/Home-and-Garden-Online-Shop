package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void deleteById() {
        Long cartItemId = 5L;

        cartItemRepository.deleteById(cartItemId);

        CartItem deletedCartItem = cartItemRepository.findById(cartItemId).orElse(null);
        assertNull(deletedCartItem);
    }
}