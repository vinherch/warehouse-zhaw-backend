package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.BarcodeMapping;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.BarcodeMappingRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author dejan.kosic
 * Service class for the barcode mapping controller
 */
@Service
@AllArgsConstructor
public class BarcodeMappingService implements HasLogger {

    private final Logger logger = getLogger();

    private final BarcodeMappingRepository barcodeMappingRepository;
    private final CsvExportService csvExportService;

    /**
     * Gets a list of all barcode mappings
     * @return list of all barcode mappings
     */
    public List<BarcodeMapping> getAllBarcodeMappings(){
        return barcodeMappingRepository.findAll();
    }

    /**
     * Gets a specific barcode mapping by id
     * @param id id of the barcode mapping
     * @return barcode mapping with provided id
     */
    public BarcodeMapping getBarcodeMappingById(long id){
        return barcodeMappingRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("barcode mapping does not exist with id: " + id));
    }
    /**
     * Gets a specific barcode mapping by ean
     * @param ean EAN of the barcode mapping
     * @return barcode mapping with provided ean
     */
    public BarcodeMapping getBarcodeMappingByEan(String ean){
        return barcodeMappingRepository.findBarcodeMappingByEan(ean).orElseThrow(()->
                new ResourceNotFoundException("barcode mapping does not exist with ean: " + ean));

    }
    /**
     * Adding a new barcode mapping to database
     * @param barcodeMapping barcode mapping to be added
     * @return added barcode mapping
     * @throws RecordAlreadyExistsException if barcode mapping already exists in database
     */
    public BarcodeMapping addNewBarcodeMapping(BarcodeMapping barcodeMapping) throws RecordAlreadyExistsException {
        Optional<BarcodeMapping> exitingBarcodeMapping = barcodeMappingRepository.findBarcodeMappingByEanAndDescription(barcodeMapping.getEan(),barcodeMapping.getDescription());
        if (exitingBarcodeMapping.isPresent()) {
            throw new RecordAlreadyExistsException("barcode mapping already exists in database!");
        }
        barcodeMappingRepository.save(barcodeMapping);
        return barcodeMappingRepository.findBarcodeMappingByEanAndDescription(barcodeMapping.getEan(),barcodeMapping.getDescription()).get();

    }

    /**
     * Modifying a barcode mapping
     * @param barcodeMapping modified barcode mapping object
     * @param id id of the barcode mapping which is to be modified
     *
     */
    public void modifyBarcodeMappingById(BarcodeMapping barcodeMapping,Long id){
        BarcodeMapping updatedBarcodeMapping = barcodeMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("barcode mapping does not exist with id: " + id));
        updatedBarcodeMapping.setEan(barcodeMapping.getEan());
        updatedBarcodeMapping.setDescription(barcodeMapping.getDescription());
        updatedBarcodeMapping.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("barcode mapping with id "+ id+" found and updating...");
        barcodeMappingRepository.save(updatedBarcodeMapping);
    }

    /**
     * Deletes a barcode mapping by id
     * @param id id of the barcode mapping to be deleted
     */
    public void deleteBarcodeMappingById(long id){
        barcodeMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("barcode mapping does not exist with id: " + id));
        logger.info("barcode mapping with id "+ id+" found and deleting...");
        barcodeMappingRepository.deleteById(id);
    }

    /**
     * Saves barcode mappings to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"BarcodeMapping.csv\"");
        csvExportService.writeBarcodeMappingToCsv(servletResponse.getWriter());
    }
}
