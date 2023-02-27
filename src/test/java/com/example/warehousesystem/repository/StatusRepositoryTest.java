package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * @author yasmin.rosskopf
 * Test for Repository class of the status entity
 * only testing specifically implemented methods
 */
@DataJpaTest
public class StatusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StatusRepository statusRepository;

    private Status active;
    /**
     * Set up before each test
     */
    @BeforeEach
    public void setup() {
        active = new Status("Active");
        entityManager.persist(active);
        entityManager.flush();
    }

    /**
     * Test to find a Status by description
     * @result verifies if the status with specific description is found
     */
    @Test
    public void returnsStatus_When_findDescription() {
        // when
        Status found = statusRepository.findStatusByDescription(active.getDescription()).get();

        // then
        assertEquals(active.getDescription(), found.getDescription());
    }

    /**
     * Test to find a Status by description, that does not exist
     * @result verifies if the status returned is empty
     */
    @Test
    public void returnsOptionalEmpty_When_findDescriptionWithNonExistingStatus() {
        // when
       assertTrue(statusRepository.findStatusByDescription("inactive").isEmpty());
    }

}