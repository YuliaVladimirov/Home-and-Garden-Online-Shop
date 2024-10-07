package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.Favorite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    void deleteById() {
        Long favoriteId = 5L;

        favoriteRepository.deleteById(favoriteId);

        Favorite deletedFavorite = favoriteRepository.findById(favoriteId).orElse(null);
        assertNull(deletedFavorite);
    }
}