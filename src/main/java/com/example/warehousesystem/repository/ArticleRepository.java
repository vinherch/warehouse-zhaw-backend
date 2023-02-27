package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author dejan.kosic
 * This is the repository for the entity "Article"
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    /**
     * Gets an article entry from the article table by description
     * @param description description of the article
     * @return Option with the article if it exists
     */
    Optional<Article> findArticleByDescription(String description);

    /**
     * Custom query for finding article entries which have a quantity under a certain limit
     * @param quantityLimit quantity limit which should be checked
     * @return a list of all article entries which fulfill the condition
     */
    @Query("SELECT a FROM Article a inner join Warehouse w on a.id=w.article.id where w.quantity <= :quantityLimit")
    List<Article> getAllArticlesWithLowQuantity(double quantityLimit);

    /**
     * Custom query for deleting article by id
     * @param id id of the article entry
     */
    @Modifying
    @Query("DELETE FROM Article WHERE id=:id")
    void deleteArticleById(long id);

}
