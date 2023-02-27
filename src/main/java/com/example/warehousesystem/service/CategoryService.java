package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Category;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.CategoryRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author dejan.kosic
 * Service class for category controller
 */

@Service
@AllArgsConstructor
public class CategoryService implements HasLogger {

    private final Logger logger = getLogger();
    private final CategoryRepository categoryRepository;
    private final CsvExportService csvExportService;

    /**
     * Gets a list of all categories
     * @return list of all categories
     */
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    /**
     * Gets a specific category by id
     * @param id id of the category
     * @return category with provided id
     */
    public Category getCategoryById(long id){
        return categoryRepository.findById(id).orElseThrow(()->
        new ResourceNotFoundException("Category does not exist with id: " + id));
    }

    /**
     * Adds a new category to database
     * @param category category to be added
     * @return added category
     * @throws RecordAlreadyExistsException if category already exists in database
     */
    public Category addNewCategory(Category category) throws RecordAlreadyExistsException {
        Optional<Category> exitingCategory = categoryRepository.findCategoryByDescription(category.getDescription());
        if (exitingCategory.isPresent()) {
            throw new RecordAlreadyExistsException("Category already exists in database!");
        }
        categoryRepository.save(category);
        return categoryRepository.findCategoryByDescription(category.getDescription()).get();

    }

    /**
     * Modifies a category
     * @param category modified category object
     * @param id id of the category which is to be modified
     */
    public void modifyCategoryById(Category category,Long id){
        Category updatedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));
        updatedCategory.setDescription(category.getDescription());
        updatedCategory.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("category with id "+ id+" found and updating...");
        categoryRepository.save(updatedCategory);
    }

    /**
     * Deletes a category by id
     * @param id id of the category to be deleted
     */
    public void deleteCategoryById(long id){
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));
        logger.info("category with id "+ id+" found and deleting...");
        categoryRepository.deleteById(id);
    }
    
    /**
     * Saves categories to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"categories.csv\"");
        csvExportService.writeCategoriesToCsv(servletResponse.getWriter());
    }

}
