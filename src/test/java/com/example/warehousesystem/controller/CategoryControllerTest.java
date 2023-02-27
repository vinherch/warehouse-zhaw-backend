package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Category;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.warehousesystem.utils.TestHelperMethods.asJson;
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyCategory;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yasmin.rosskopf
 * Test for controller class for entity "Category"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 */
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CategoryService categoryService;
    private Category category;

    /**
     * instantiates up a dummy category to test on before each test
     */
    @BeforeEach
    public void setup() {
        category = createDummyCategory();
    }

    /**
     * Test for GET endpoint to fetch all categories
     *
     * @result returns a response with 200 OK, body with list of categories
     */
    @Test
    public void getsResponseWithListOfAllCategories_When_getAllCategoriesIsCalled() throws Exception {
        List<Category> CategoryList = new ArrayList<>();
        CategoryList.add(category);
        when(categoryService.getAllCategories()).thenReturn(CategoryList);
        mvc.perform(get("/v1/categories")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(category.getDescription())));
    }

    /**
     * Test for GET endpoint to an category by ID
     *
     * @result returns a response with 200 OK, body with category with specific id
     */
    @Test
    public void getsResponseWithCategoryWithSpecificId_When_getCategoryByIdIsCalled() throws Exception {
        when(categoryService.getCategoryById(1)).thenReturn(category);
        mvc.perform(get("/v1/categories/" + category.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(category.getDescription())));
    }

    /**
     * Test for DELETE endpoint to delete an category by ID
     *
     * @result returns a response with 204 NO_CONTENT, and calls categoryService to delete category
     */
    @Test
    public void getsResponseWithNoContent_When_deleteCategoryByIdIsCalled() throws Exception {
        doNothing().when(categoryService).deleteCategoryById(1L);
        mvc.perform(delete("/v1/categories/" + category.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }

    /**
     * Test for POST endpoint to add a new category
     *
     * @result returns a response with 201 CREATED and body with the new category
     */
    @Test
    public void getsResponseWithCategoryWithId_When_addCategoryIsCalled() throws Exception {
        when(categoryService.addNewCategory(category)).thenReturn(category);
        mvc.perform(post("/v1/categories").content(asJson(category)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }

    /**
     * Test for POST endpoint to add a new category
     *
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addCategoryIsCalled() throws Exception {
        //when
        when(categoryService.addNewCategory(category)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/categories")
                                .content(asJson(category))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }

    /**
     * Test for PUT endpoint to modify an existing category
     *
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithCategoryWithSpecificId_When_modifyCategoryByIdIsCalled() throws Exception {
        doNothing().when(categoryService).modifyCategoryById(category, 1L);
        mvc.perform(put("/v1/categories/" + category.getId())
                        .content(asJson(category))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }
}