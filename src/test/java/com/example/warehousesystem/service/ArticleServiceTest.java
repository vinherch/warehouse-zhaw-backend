package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.ArticleRepository;
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
 * Test for service class of entity "Article"
 */
@SpringBootTest(classes = ArticleServiceTest.class)
public class ArticleServiceTest {
    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private CsvExportService csvExportService;

    private ArticleService articleService;
    private Article article;


    /**
     * instantiates the articleService before each test
     */
    @BeforeEach
    void setUp() {
        articleService = new ArticleService(articleRepository, csvExportService);
        article = new Article();
        article.setId(1L);
        article.setDescription("Nike");
        article.setAmount(200);
    }

    /**
     * Test for service to fetch all articles
     * @result returns a list of articles
     */
    @Test
    public void getsAListOfAllArticles_When_getAllArticlesIsCalled() {
        List<Article> articles = new ArrayList<>();
        articles.add(article);
        when(articleRepository.findAll()).thenReturn(articles);
        List<Article> result = articleService.getAllArticles();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch article with specific id
     * @result returns an optional article
     */
    @Test
    public void getsArticleWithSpecificId_When_getArticleByIdIsCalled() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        Article result = articleService.getArticleById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to fetch article with specific id that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsAResourceNotFoundException_When_getArticleByIdWithNonExistingIdIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            when(articleRepository.findById(1L)).thenThrow(new ResourceNotFoundException("Record not found"));
            articleService.getArticleById(1);
        });
    }

    /**
     * Test for service to delete an article by ID
     * @result calls articleRepository to delete article
     */
    @Test
    public void callsArticleRepositoryToDeleteArticleWithSpecificId_When_deleteArticleByIdIsCalled() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).deleteArticleById(1);
        articleService.deleteArticleById(1L);
        verify(articleRepository, times(1)).deleteArticleById(1L);
    }

    /**
     * Test for service to add an article that already exists
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addArticleByIdThatExistsIsCalled() {
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(articleRepository.findArticleByDescription(article.getDescription())).thenReturn(Optional.of(article));
            articleService.addArticle(article);
        });
    }

    /**
     * Test for service to add a new article
     * @throws if the amount is lower or equals zero
     * @result returns the new article
     */
    @Test
    public void returnsTheNewArticle_When_addArticleByIdThatExistsIsCalled() throws RecordAlreadyExistsException, InvalidFormatEntryException {
        when(articleRepository.findArticleByDescription(article.getDescription())).thenReturn(Optional.empty());
        when(articleRepository.save(article)).thenReturn(article);
        Article newArticle = articleService.addArticle(article);
        assertEquals(article, newArticle);
    }

    /**
     * Test for service to delete an article that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_addArticleByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(articleRepository).deleteArticleById(1);
            articleService.deleteArticleById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if article service fails
     * @result calls the csvExportService for articles
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        articleService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"articles.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeArticlesToCsv(response.getWriter());
    }

    /**
     * Test for service to modify an article with specific id
     * @result calls articleRepository to save updated article
     * @throws if entry is lower or same as zero for the amount
     */
    @Test
    public void callsRepositoryToModifyArticleWithSpecificId_When_modifyArticleByIdIsCalled() throws InvalidFormatEntryException {
        Article modifiedArticle = new Article();
        modifiedArticle.setId(2L);
        modifiedArticle.setDescription("Adidas");
        modifiedArticle.setAmount(199);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(articleRepository.save(modifiedArticle)).thenReturn(modifiedArticle);

        assertNotEquals(article, modifiedArticle);
        articleService.modifyArticleById(modifiedArticle, 1L);

        assertEquals(article, modifiedArticle);
        verify(articleRepository, times(1)).save(article);
    }
}