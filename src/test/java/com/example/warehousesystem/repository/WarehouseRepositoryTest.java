package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
/**
 * @author yasmin.rosskopf
 * Test for Repository class of the warehouse entity
 * only testing specifically implemented methods
 */
@DataJpaTest
class WarehouseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private Article article1;
    private Location location;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        //given
        Status status = new Status("active");
        Category category = new Category("Schuhe");
        Currency currency = new Currency("CHF", "Schweiz");
        entityManager.persist(status);
        entityManager.persist(category);
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

        location = new Location("A", 1, 1);
        entityManager.persist(location);
        entityManager.flush();

        Warehouse warehouse1 = new Warehouse(article1, location, 200);
        entityManager.persist(warehouse1);
        entityManager.flush();
    }

    /**
     * Test to find a warehouse entry by Article and Location
     * @result verifies if the warehouse entry with specific id is removed
     */
    @Test
    void returnsWarehouse_When_findWarehouseByArticleAndLocation() {
        //when
        Warehouse found = warehouseRepository.findWarehouseByArticleAndLocation(article1, location).get();

        // then
        assertEquals(article1.getDescription(), found.getArticle().getDescription());
        assertEquals(location.getAisle(), found.getLocation().getAisle());
    }

    /**
     * Test to delete a warehouse entry by id
     * @result verifies if the warehouse entry with specific id is removed
     */
    @Test
    void warehouseRemoved_When_deleteWarehouseById() {
        //when
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst().get();
        warehouseRepository.deleteWarehousebyId(warehouse.getId());

        // then
        assertFalse(warehouseRepository.existsById(warehouse.getId()));
    }

}