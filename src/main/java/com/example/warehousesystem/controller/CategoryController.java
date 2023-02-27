package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Category;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CategoryService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author dejan.kosic
 * Controller class that provides CRUD-operation endpoints in regard to the category entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 */
@RestController
@RequestMapping(path="/v1/categories")
public class CategoryController implements HasLogger{

    private final Logger logger = getLogger();

    private final CategoryService categoryService;

    /**
     * Custom constructor for the category controller
     * @param categoryService service for the category controller
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET endpoint to fetch all categories in the database
     * @return a list of all categories
     */
    @GetMapping
    public List<Category> getAllCategoryEntries(){
        logger.info("get all categories");
        return categoryService.getAllCategories();
    }

    /**
     * GET endpoint to fetch a specific category by ID in the database
     * @param id the id of the category that is to be found
     * @return the specific category
     */
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable long id){
        logger.info("get categories with id "  + id);
        return categoryService.getCategoryById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all categories in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getCategoryAsCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with categories");
        this.categoryService.getCsv(servletResponse);
    }
    /**
     * POST endpoint to create a new category in the database
     * @param category the category object to be created
     * @return the created category
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category insertNewCategory(@RequestBody Category category) throws RecordAlreadyExistsException {
        logger.info("creating category...");
        Category insertedCategory = categoryService.addNewCategory(category);
        logger.info("category with id: " + insertedCategory.getId()+" successfully created!");
        return insertedCategory;
    }

    /**
     * PUT endpoint to update a specific category by ID in the database
     * @param id the id of the category that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyCategoryById(@RequestBody Category category, @PathVariable long id){
        logger.info("updating category with id: " + id+"...");
        categoryService.modifyCategoryById(category,id);
        logger.info("category with id: " + id + " updated!");
    }

    /**
     * DELETE endpoint to delete an existing category in the database
     * @param id the id of the specific category to be deleted
     */
    @DeleteMapping  ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryById(@PathVariable("id") long id){
        logger.info("deleting category with id "  + id+"...");
        categoryService.deleteCategoryById(id);
        logger.info("category with id "+ id +" successfully deleted!");
    }



}
