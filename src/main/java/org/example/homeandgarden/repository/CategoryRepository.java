package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findCategoryByName(String name);
}
