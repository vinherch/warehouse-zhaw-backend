package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.ArticleService;
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
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyArticle;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author yasmin.rosskopf
 * Test for controller class for entity "Article"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 */
@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ArticleService articleService;

    /**
     * Test for GET endpoint to fetch all articles
     * @result returns a response with 200 OK, body with list of articles
     */
    @Test
    public void getsResponseWithListOfAllArticles_When_getAllArticlesIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        Article article1 = createDummyArticle();
        article1.setDescription("Muesli");
        List<Article> articleList = new ArrayList<>();
        articleList.add(article);
        articleList.add(article1);

        //when
        when(articleService.getAllArticles()).thenReturn(articleList);

        //then
        mvc.perform(get("/v1/articles")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}, {}]"))
                .andExpect(jsonPath("$[0].description", is(article.getDescription())));
    }

    /**
     * Test for GET endpoint to an article by ID
     * @result returns a response with 200 OK, body with article with specific id
     */
    @Test
    public void getsResponseWithArticleWithSpecificId_When_getArticleByIdIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        //when
        when(articleService.getArticleById(1)).thenReturn(article);
        //then
        mvc.perform(get("/v1/articles/" + article.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(article.getDescription())));
    }

    /**
     * Test for DELETE endpoint to delete an article by ID
     * @result returns a response with 204 NO_CONTENT, and calls articleService to delete article
     */
    @Test
    public void getsResponseWithNoContent_When_deleteArticleByIdIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        //when
        doNothing().when(articleService).deleteArticleById(1L);
        //then
        mvc.perform(delete("/v1/articles/" + article.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
        verify(articleService, times(1)).deleteArticleById(1L);
    }

    /**
     * Test for POST endpoint to add a new article
     * @result returns a response with 201 CREATED and body with the new article
     */
    @Test
    public void getsResponseWithArticleWithId_When_addArticleIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        //when
        when(articleService.addArticle(article)).thenReturn(article);
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/articles")
                                .content(asJson(article))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(article.getDescription())));
    }

    /**
     * Test for POST endpoint to add a new article
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addArticleIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        //when
        when(articleService.addArticle(article)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/articles")
                                .content(asJson(article))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }

    /**
     * Test for PUT endpoint to modify an existing article
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithArticleWithSpecificId_When_modifyArticleByIdIsCalled() throws Exception {
        //given
        Article article = createDummyArticle();
        //when
        doNothing().when(articleService).modifyArticleById(article, 1L);
        //then
        mvc.perform(put("/v1/articles/" + article.getId()).content(asJson(article)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }
}
