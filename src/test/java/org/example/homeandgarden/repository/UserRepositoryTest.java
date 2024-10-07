package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail() {
        String email = "benjamindietrich@example.com";
        String notExistingEmail = "wrongemail@example.com";

        Boolean existsByEmail = userRepository.existsByEmail(email);
        assertTrue(existsByEmail);

        Boolean doesNotExistsByEmail = userRepository.existsByEmail(notExistingEmail);
        assertFalse(doesNotExistsByEmail);
    }


    @Test
    void findByEmail() {

        String email = "benjamindietrich@example.com";
        String notExistingEmail = "wrongemail@example.com";

        User foundByEmail = userRepository.findByEmail(email).orElse(null);
        assertNotNull(foundByEmail);

        User notFoundByEmail = userRepository.findByEmail(notExistingEmail).orElse(null);
        assertNull(notFoundByEmail);
    }
}