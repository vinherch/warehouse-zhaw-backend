package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Warehouse;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CSVImportService;
import com.example.warehousesystem.service.WarehouseService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.warehousesystem.utils.CsvCreator.hasCSVFormat;

/**
 * @author yasmin.rosskopf
 * Controller class that provides CRUD-operation endpoints in regard to the warehouse entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 * */
@RestController
@RequestMapping("/v1/warehouses")
public class WarehouseController implements HasLogger {
    private final Logger logger = getLogger();
    private final WarehouseService warehouseService;
    private final CSVImportService csvImportService;

    /**
     * Custom constructor for the warehouse controller
     * @param warehouseService service for the warehouse controller
     * @param csvImportService service for the csv upload
     */
    public WarehouseController(WarehouseService warehouseService, CSVImportService csvImportService) {
        this.warehouseService = warehouseService;
        this.csvImportService = csvImportService;
    }

    /**
     * GET endpoint to fetch all warehouses entries in the database
     * @return a list of all warehouses
     */
    @GetMapping
    public List<Warehouse> getAllWarehouses() {

        logger.info("get all warehouse entries");
        return warehouseService.getAllWarehouses();
    }

    /**
     * GET endpoint to fetch a specific warehouse entry by ID in the database
     * @param id the id of the warehouse that is to be found
     * @return the specific warehouse
     */
    @GetMapping("/{id}")
    public Warehouse getWarehouseById(@PathVariable long id) {
        logger.info("get warehouse entry with id "+id);
        return warehouseService.getWarehouseById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all warehouse entries in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getWarehouseAsCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with warehouse entries");
        this.warehouseService.getCsv(servletResponse);
    }

    /**
     * PUT endpoint to update a specific warehouse by ID in the database
     * @throws if quantity entered is equals or lower then zero
     * @param id the id of the warehouse that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyWarehouseById(@RequestBody Warehouse warehouse, @PathVariable long id) throws InvalidFormatEntryException {
        logger.info("updating warehouse entry with id: " + id+"...");
        warehouseService.modifyWarehouseById(warehouse, id);
        logger.info("warehouse entry with id: " + id + " updated!");
    }

    /**
     * POST endpoint to create a new warehouse in the database
     * @param warehouse the warehouse in JSON to be created
     * @throws if record already exist or if quantity entered is equals or lower then zero
     * @return the created warehouse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse addWarehouse(@RequestBody Warehouse warehouse) throws RecordAlreadyExistsException, InvalidFormatEntryException {
        logger.info("creating warehouse entry...");
        Warehouse insertedWarehouse = warehouseService.addNewWarehouse(warehouse);
        logger.info("warehouse entry with id: " + insertedWarehouse.getId()+" successfully created!");
        return insertedWarehouse;
    }

    /**
     * POST endpoint to upload a csv file to add or modify the whole warehouse with all it's dependencies
     * @param file the file from the request to this endpoint
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message;

        if (hasCSVFormat(file)) {
            try {
                csvImportService.saveAllEntitiesToDBFromCSV(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


    /**
     * DELETE endpoint to delete an existing warehouse in the database
     * @param id the id of the specific warehouse to be deleted
     */
    @DeleteMapping  ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeWarehouseById(@PathVariable("id") long id){
        logger.info("deleting warehouse entry with id " + id+"...");
        warehouseService.deleteWarehouseById(id);
        logger.info("warehouse entry with id "+ id +" successfully deleted!");
    }
}
