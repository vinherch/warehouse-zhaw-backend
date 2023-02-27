package com.example.warehousesystem;

import com.example.warehousesystem.entities.Category;
import com.example.warehousesystem.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author dejan.kosic
 * Class for an integration test for the whole application, contains only one sample test
 */
@ActiveProfiles(profiles = {"dev", "h2"})
@SpringBootTest(classes = ManagementToolApplication.class)
class ManagementToolTests {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * An integration test to test if the application starts
     * @result calls categoryRepository to save new category
     */
    @Test
    public void givenPersistedCategory_whenFindById_thenCategoryIsFound() {
        Category category = new Category("Gummibärchen");

        category.setArticles(Collections.emptyList());

        categoryRepository.save(category);

        assertEquals(category.getDescription(), categoryRepository.findById(category.getId()).get().getDescription());
    }
}
