package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * @author yasmin.rosskopf
 * Test for Repository class of the location entity
 * only testing specifically implemented methods
 */
@DataJpaTest
public class LocationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LocationRepository locationRepository;

    private Location location;
    /**
     * Set up before each test
     */
    @BeforeEach
    public void setup() {
        // given
        location =  new Location("A", 1,55);
        entityManager.persist(location);
        entityManager.flush();
    }

    /**
     * Test to find a Location by Aisle, Tray and Shelf
     * @result verifies if the location with specific Aisle, tray and Shelf is found
     */
    @Test
    public void returnsLocation_When_findByAisleTrayAndShelf() {
        // when
        Location found = locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), location.getTray()).get();

        // then
        assertEquals(location.getAisle(), found.getAisle());
        assertEquals(location.getTray(), found.getTray());
        assertEquals(location.getShelf(), found.getShelf());
    }

    /**
     * Test to find a Location by Aisle, Tray and Shelf, whereby the Aisle does not exist
     * @result verifies if the location is empty
     */
    @Test
    public void returnsEmptyOptional_When_findByAisleTrayAndShelfWithNonExistingAisle() {
        // when
        assertTrue(locationRepository.findLocationByAisleAndShelfAndTray("a", location.getShelf(), location.getTray()).isEmpty());
    }

    /**
     * Test to find a Location by Aisle, Tray and Shelf, whereby the Shelf does not exist
     * @result verifies if the location is empty
     */
    @Test
    public void returnsEmptyOptional_When_findByAisleTrayAndShelfWithNonExistingShelf() {
        // when
        assertTrue(locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), 5, location.getTray()).isEmpty());
    }

    /**
     * Test to find a Location by Aisle, Tray and Shelf, whereby the Tray does not exist
     * @result verifies if the location is empty
     */
    @Test
    public void returnsEmptyOptional_When_findByAisleTrayAndShelfWithNonExistingTray() {
        // when
        assertTrue(locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), 99).isEmpty());
    }

}