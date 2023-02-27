package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.ArticleRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author yasmin.rosskopf
 * Service class for the article controller
 */

@Service
@AllArgsConstructor
public class ArticleService implements HasLogger {

    private final Logger logger = getLogger();
    private final ArticleRepository articleRepository;

    private final CsvExportService csvExportService;


    /**
     * Gets a list of all articles
     * @return a list of all articles
     */
    public List<Article> getAllArticles(){
        return articleRepository.findAll();
    }

    /**
     * Gets a specific article by id
     * @param id id of the article
     * @return article with provided id
     */
    public Article getArticleById(long id){
        return articleRepository.findById(id).orElseThrow(()->
        new ResourceNotFoundException("Article does not exist with id: " + id));
    }

    /**
     * Saves articles to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"articles.csv\"");
        csvExportService.writeArticlesToCsv(servletResponse.getWriter());
    }

    /**
     * Adding an article to database
     * @param article article to be added
     * @return the added article
     * @throws RecordAlreadyExistsException if article already exists in the database
     */
    public Article addArticle(Article article) throws RecordAlreadyExistsException, InvalidFormatEntryException {
        Optional<Article> existingArticle = articleRepository.findArticleByDescription(article.getDescription());
        if (existingArticle.isPresent()) {
            throw new RecordAlreadyExistsException("Article already exists in database!");
        }
        if(article.getAmount()<=0){
            logger.error("Not a positive number entered!");
            throw new InvalidFormatEntryException("Please enter a positive number for the amount!");
        }
            articleRepository.save(article);
        //important: don't return the articleRepository.save(article) immediately, because it's dependencies are set to null (e.g. category = null)
            return articleRepository.findArticleByDescription(article.getDescription()).orElse(article);
        }

    /**
     * Modifying an article
     * @param article new article object
     * @param id id of the article which is to be modified
     */
    public void modifyArticleById(Article article, Long id) throws InvalidFormatEntryException {
        Article updatedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article does not exist with id: " + id));
        if(article.getAmount()<=0){
            logger.error("Not a positive number entered!");
            throw new InvalidFormatEntryException("Please enter a positive number for the amount!");
        }
        logger.info("article with id " + id + " found and updating...");
        updatedArticle.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updatedArticle.setAmount(article.getAmount());
        updatedArticle.setDescription(article.getDescription());
        updatedArticle.setCategory(article.getCategory());
        updatedArticle.setStatus(article.getStatus());
        updatedArticle.setCurrency(article.getCurrency());

        articleRepository.save(updatedArticle);


    }

    /**
     * Deletes an article by id
     * @param id id of the article to be deleted
     */
    @Transactional
    public void deleteArticleById(long id) {
        articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article does not exist with id: " + id));
        logger.info("article with id " + id + " found and deleting...");
        articleRepository.deleteArticleById(id);
    }

}
