package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("DELETE FROM Favorite favorite " +
            "WHERE favorite.favoriteId = :id")
    void deleteById(Long id);


}
