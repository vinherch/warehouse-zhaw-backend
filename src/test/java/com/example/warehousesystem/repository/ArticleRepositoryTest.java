package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yasmin.rosskopf
 * Test class for article respository testing
 */
@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    private Article article1;
    private Article article2;

    /**
     * Set up for each test
     */
    @BeforeEach
    void setUp() {
        Status status = new Status("active");
        entityManager.persist(status);
        Category category = new Category("Schuhe");
        entityManager.persist(category);
        Currency currency = new Currency("CHF", "Schweiz");
        entityManager.persist(currency);

        entityManager.flush();


        article1 = new Article();
        article1.setAmount(19.9);
        article1.setDescription("Adidas Schluffen");
        article1.setStatus(status);
        article1.setCategory(category);
        article1.setCurrency(currency);
        entityManager.persist(article1);
        entityManager.flush();

        article2 = new Article();
        article2.setAmount(299);
        article2.setDescription("Airforce Nike");
        article2.setStatus(status);
        article2.setCategory(category);
        article2.setCurrency(currency);
        entityManager.persist(article2);
        entityManager.flush();

    }

    /**
     * Test to find an article by description
     * @result verifies if the article with specific description is found
     */
    @Test
    void findsArticle_When_findArticleByDescription() {
        //when
        Article found = articleRepository.findArticleByDescription(article1.getDescription()).get();

        // then
        assertEquals(found.getDescription(), article1.getDescription());
    }

    /**
     * Test to find all article with a low quantity
     * @result verifies if the articles found are the correct ones
     */
    @Test
    void getsAllArticlesWithQuantityLower100_When_getAllArticlesWithLowQuantityIsCalled() {
        //given
        Location location = new Location("A", 1, 1);
        entityManager.persist(location);
        entityManager.flush();

        Warehouse warehouse1 = new Warehouse(article1, location, 200);
        Warehouse warehouse2 = new Warehouse(article2, location, 100);
        entityManager.persist(warehouse1);
        entityManager.persist(warehouse2);
        entityManager.flush();

        //when
        List<Article> found = articleRepository.getAllArticlesWithLowQuantity(100);

        // then
        assertEquals( 1, found.size());
        assertEquals(article2.getDescription(), found.get(0).getDescription());

    }

    /**
     * Test to delete an article by ID
     * @result verifies if the article with specific id is removed
     */
    @Test
    void articleNotExistsAnymore_When_deleteArticleById() {
        //when

        Article article = articleRepository.findAll().stream().findFirst().get();
        articleRepository.deleteArticleById(article.getId());

        // then
        assertFalse(articleRepository.existsById(article.getId()));
    }

}