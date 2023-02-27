package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.BarcodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dejan.kosic
 * Test class for the Barcode Mapping entity
 */
@Repository
public interface BarcodeMappingRepository extends JpaRepository<BarcodeMapping,Long> {

    /**
     * Finds the barcode mapping by ean and description
     * @param ean ean of the article in the barcode mapping entity
     * @param description of the barcode mapping entity article
     * @return Option with the barcode mapping entity if it exists
     */
    Optional<BarcodeMapping> findBarcodeMappingByEanAndDescription(String ean, String description);

/**
 * Finds the barcode mapping by ean
 * @param ean ean of the article in the barcode mapping entity
 * @return Option with the barcode mapping entity if it exists
 */
    Optional<BarcodeMapping> findBarcodeMappingByEan(String ean);
}
