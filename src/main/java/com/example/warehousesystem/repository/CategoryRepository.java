package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dejan.kosic
 * This is the repository for the entity "Category"
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    /**
     * Find a category by description
     * @param description description of the category which should be found
     * @return Option with the category if it exists
     */
    Optional<Category> findCategoryByDescription(String description);
}
