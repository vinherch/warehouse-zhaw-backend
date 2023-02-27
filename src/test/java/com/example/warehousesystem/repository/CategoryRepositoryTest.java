package com.example.warehousesystem.repository;

import static org.junit.jupiter.api.Assertions.*;


import com.example.warehousesystem.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author yasmin.rosskopf
 * Test for Repository class of the category entity
 * only testing specifically implemented methods
 */
@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setup() {
        // given
        category = new Category("Kleider");
        entityManager.persist(category);
        entityManager.flush();
    }

    /**
     * Test to find a category by description
     *
     * @result verifies if the category with specific id is found
     */
    @Test
    public void returnsCategory_When_findByDescription() {
        // when
        Category found = categoryRepository.findCategoryByDescription(category.getDescription()).get();

        // then
        assertEquals(category.getDescription(), found.getDescription());
    }

    /**
     * Test to find a category by description, whereby the description does not exist
     *
     * @result verifies if the category is empty
     */
    @Test
    public void returnsOptionalEmpty_When_FindByDescription() {
        assertTrue(categoryRepository.findCategoryByDescription("Hemden").isEmpty());
    }
}