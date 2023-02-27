package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.ArticleService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author yasmin.rosskopf
 * Controller class that provides CRUD-operation endpoints in regard to the article entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 */

@RestController
@RequestMapping("/v1/articles")
public class ArticleController implements HasLogger {
    private final Logger logger = getLogger();
    private final ArticleService articleService;

    /**
     * Custom constructor for the article controller
     * @param articleService service for the article controller
     */
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * GET endpoint to fetch all articles in the database
     * @return a list of all articles
     */
    @GetMapping
    public List<Article> getAllArticles() {
        logger.info("get all articles");
        return  articleService.getAllArticles();
    }

    /**
     * GET endpoint to fetch a specific article by ID in the database
     * @param id the id of the article that is to be found
     * @return the specific article
     */
    @GetMapping("/{id}")
    public Article getArticleById(
            @PathVariable Long id
    ) {
       logger.info("get article with id "  + id);
       return this.articleService.getArticleById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all articles in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getArticleCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with articles");
        this.articleService.getCsv(servletResponse);
    }
    /**
     * POST endpoint to create a new article in the database
     * @param article the article object to be created
     * @throws  RecordAlreadyExistsException if record already exists
     * @throws  InvalidFormatEntryException if the amount is lower or equals zero
     * @return the created article
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article addArticle(@RequestBody Article article) throws RecordAlreadyExistsException, InvalidFormatEntryException {
        logger.info("creating article...");
        Article insertedArticle = articleService.addArticle(article);
        logger.info("article with"+ insertedArticle.getId()+" successfully created!");
        return insertedArticle;
    }

    /**
     * PUT endpoint to update a specific article by ID in the database
     * @throws InvalidFormatEntryException if the amount is lower or equals zero
     * @param id the id of the article that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyArticleById(@RequestBody Article article, @PathVariable long id) throws InvalidFormatEntryException {
        logger.info("updating article with id: " + id+"...");
        articleService.modifyArticleById(article,id);
        logger.info("article with id: " + id + " updated!");
    }


    /**
     * DELETE endpoint to delete an existing article in the database
     * @param id the id of the specific article to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable Long id) {
            logger.info("deleting article with id "  + id+"...");
            articleService.deleteArticleById(id);
            logger.info("article with id "+ id +" successfully deleted!");
    }
}