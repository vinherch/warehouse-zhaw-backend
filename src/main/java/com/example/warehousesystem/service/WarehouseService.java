package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Warehouse;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.WarehouseRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author yasmin.rosskopf
 * Service class for the warehouse controller
 */
@Service
@AllArgsConstructor
public class WarehouseService implements HasLogger {
    private final Logger logger = getLogger();
    private final WarehouseRepository warehouseRepository;
    private CsvExportService csvExportService;

    /**
     * Gets a list of all warehouse entries
     * @return list of all warehouses entries
     */
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    /**
     * Gets a specific warehouse entry by id
     * @param id id of the warehouse entry
     * @return warehouse entry with provided id
     */
    public Warehouse getWarehouseById(long id) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        if (warehouse.isPresent()) {
            return warehouse.get();
        }
        throw new ResourceNotFoundException("Warehouse does not exist with id: " + id);
    }

    /**
     * Adds a new warehouse entry to database
     * @param warehouse warehouse entry to be added
     * @return added warehouse entry
     * @throws RecordAlreadyExistsException if warehouse already exists in database
     */
    public Warehouse addNewWarehouse(Warehouse warehouse) throws RecordAlreadyExistsException, InvalidFormatEntryException {
        Optional<Warehouse> existingWarehouse = warehouseRepository.
                findWarehouseByArticleAndLocation(warehouse.getArticle(),warehouse.getLocation());
        if(existingWarehouse.isPresent()){
            throw new RecordAlreadyExistsException("Warehouse already exists in database!");
        }
        if(warehouse.getQuantity()<=0){
            logger.error("Not a positive number entered!");
            throw new InvalidFormatEntryException("Please enter a positive number for the quantity!");
        }
        warehouseRepository.save(warehouse);
        //important: don't return the warehouseRepository.save(warehouse) immediately, because it's dependencies are set to null (e.g. article = null)
        return warehouseRepository.findWarehouseByArticleAndLocation(warehouse.getArticle(), warehouse.getLocation()).orElse(warehouse);
    }

    /**
     * Modifies a warehouse entry
     * @param warehouse modified warehouse entry object
     * @param id id of the warehouse entry to be modified
     */
    public void modifyWarehouseById(Warehouse warehouse, Long id) throws InvalidFormatEntryException {
        Warehouse updatedWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse does not exist with id: " + id));
        if(warehouse.getQuantity()<=0){
            logger.error("Not a positive number entered!");
            throw new InvalidFormatEntryException("Please enter a positive number for the quantity!");
        }
        logger.info("warehouse with id "+ id+" found and updating...");
        updatedWarehouse.setLocation(warehouse.getLocation());
        updatedWarehouse.setQuantity(warehouse.getQuantity());
        updatedWarehouse.setArticle(warehouse.getArticle());
        updatedWarehouse.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        warehouseRepository.save(updatedWarehouse);
    }


    /**
     * deletes warehouse entry with provided id
     * @param id id of the warehouse to be deleted
     */
    @Transactional
    public void deleteWarehouseById(long id) {
        warehouseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Warehouse does not exist with id: " + id));
        logger.info("warehouse with id "+ id+" found and deleting...");
        warehouseRepository.deleteWarehousebyId(id);
    }
    
    /**
     * Saves warehouses entries to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"warehouses.csv\"");
        csvExportService.writeWarehouseToCsv(servletResponse.getWriter());
    }
}
