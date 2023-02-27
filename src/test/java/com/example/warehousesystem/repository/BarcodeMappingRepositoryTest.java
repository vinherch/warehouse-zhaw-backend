package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.BarcodeMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author dejan.kosic
 * Test class for barcode mapping repository
 */
@DataJpaTest
public class BarcodeMappingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BarcodeMappingRepository barcodeMappingRepository;

    private BarcodeMapping barcodeMapping;

    /**
     * Setup for the barcode mapping repository testing
     */
    @BeforeEach
    public void setup() {
        // given
        barcodeMapping = new BarcodeMapping("9780201379624","Skisachen");
        entityManager.persist(barcodeMapping);
        entityManager.flush();
    }
    /**
     * Test of the repository function findBarcodeMappinByEan
     * @result verifies that the barcode mapping entry is found
     *
     */
    @Test
    public void whenFindByEan_thenReturnBarcodeMapping() {
        // when
        BarcodeMapping found = barcodeMappingRepository.findBarcodeMappingByEan(barcodeMapping.getEan()).get();

        // then
        assertEquals(barcodeMapping.getEan(),found.getEan());
    }
    /**
     * Test of the repository function findBarcodeMappinByEanAndDescription
     * @result verifies that the barcode mapping entry is found
     */
    @Test
    public void whenFindByEanAndDescription_thenReturnBarcodeMapping() {
        // when
        BarcodeMapping found = barcodeMappingRepository.findBarcodeMappingByEanAndDescription(barcodeMapping.getEan(), barcodeMapping.getDescription()).get();

        // then
        assertEquals(barcodeMapping.getEan(),found.getEan());
        assertEquals(barcodeMapping.getDescription(), found.getDescription());
    }
    /**
     * Test of the repository function findBarcodeMappinByEan
     * @result verifies that the Option is empty when no barcode mapping entry is found
     */
    @Test
    public void whenFindByEan_barcodeMappingNotFound_thenReturnOptionalEmpty() {
        assertTrue(barcodeMappingRepository.findBarcodeMappingByEan("1234").isEmpty());
    }
    /**
     * Test of the repository function findBarcodeMappingByEanAndDescription
     * @result verifies that the Option is empty when no barcode mapping entry is found
     */
    @Test
    public void whenFindByEanAndDescription_barcodeMappingNotFound_thenReturnOptionalEmpty() {
        assertTrue(barcodeMappingRepository.findBarcodeMappingByEanAndDescription("1234","TestEmpty").isEmpty());
    }
}
