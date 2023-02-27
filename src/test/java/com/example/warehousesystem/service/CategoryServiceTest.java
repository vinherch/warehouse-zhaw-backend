package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Category;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


/**
 * @author yasmin.rosskopf
 * Test for service class of entity "Category"
 */
@SpringBootTest(classes = CategoryService.class)
public class CategoryServiceTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CsvExportService csvExportService;

    private CategoryService categoryService;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        categoryService= new CategoryService(categoryRepository, csvExportService);
    }

    /**
     * Test for service to fetch all categories
     * @result returns a list of categories
     */
    @Test
    public void getsAListOfAllCategories_When_getAllCategoriesIsCalled() {
        Category category = getCategory();
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        List<Category> result = categoryService.getAllCategories();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch category with specific id
     * @result returns an optional category
     */
    @Test
    public void getsCategoryWithSpecificId_When_getCategoryByIdIsCalled() {
        Category category = getCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Category result = categoryService.getCategoryById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to add an category that already exists
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addCategoryByIdThatExistsIsCalled() {
        Category category = getCategory();
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(categoryRepository.findCategoryByDescription(category.getDescription())).thenReturn(Optional.of(category));
            categoryService.addNewCategory(category);
        });
    }

    /**
     * Test for service to add a new category
     * @result returns the new category
     */
    @Test
    public void returnsTheNewCategory_When_addCategoryByIdThatExistsIsCalled() throws RecordAlreadyExistsException {
        Category category = getCategory();
        when(categoryRepository.findCategoryByDescription(category.getDescription())).thenReturn(Optional.empty()).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        Category newCategory = categoryService.addNewCategory(category);
        assertEquals(category, newCategory);
    }

    /**
     * Test for service to delete an category by ID
     * @result calls categoryRepository to delete category
     */
    @Test
    public void callsCategoryRepositoryToDeleteCategoryWithSpecificId_When_deleteCategoryByIdIsCalled() {
        Category category = getCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);
        categoryService.deleteCategoryById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for service to delete a category that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_addCategoryByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(categoryRepository).deleteById(1L);
            categoryService.deleteCategoryById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if category service fails
     * @result calls the csvExportService for categories
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        categoryService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"categories.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeCategoriesToCsv(response.getWriter());
    }
    
    /**
     * Test for service to modify a category with specific id
     * @result calls categoryRepository to save updated category
     */
    @Test
    public void callsRepositoryToModifyCategoryWithSpecificId_When_modifyCategoryByIdIsCalled() {
        Category category = getCategory();
        Category modifiedCategory = new Category();
        modifiedCategory.setId(2L);
        modifiedCategory.setDescription("Hemden");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(modifiedCategory)).thenReturn(modifiedCategory);

        assertNotEquals(category, modifiedCategory);
        categoryService.modifyCategoryById(modifiedCategory, 1L);

        assertEquals(category, modifiedCategory);
        verify(categoryRepository, times(1)).save(category);
    }

    /**
     * instantiates a dummy category for the tests
     * @return a category
     */
    private Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setDescription("Schuhe");
        return category;
    }
}