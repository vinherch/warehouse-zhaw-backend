package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Status;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.StatusService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author dejan.kosic
 *  Controller class that provides CRUD-operations endpoints in regard to the status entity
 *  the RestController annotation ensures that the object returned is automatically serialized into JSON and
 *  passed back into the HttpResponse object
 */
@RestController
@RequestMapping(path="/v1/statuses")
public class StatusController implements HasLogger {

    private final Logger logger = getLogger();
    private final StatusService statusService;

    /**
     * Custom constructor for the status controller
     * @param statusService service for the status controller
     */
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }
    /**
     * GET endpoint to fetch all statuses in the database
     * @return a list of all statuses
     */
    @GetMapping
    public List<Status> getAllStatusEntries() {
        logger.info("returns all categories");
        return statusService.getAllStatus();
    }
    /**
     * GET endpoint to fetch a specific status by ID in the database
     * @param id the id of the status that is to be found
     * @return the specific status
     */
    @GetMapping("/{id}")
    public Status getStatusById(@PathVariable long id) {
        logger.info("get categories with id " + id);
        return statusService.getStatusById(id);
    }
    /**
     * GET endpoint to fetch a csv-file with all status in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getStatusAsCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with statuses");
        this.statusService.getCsv(servletResponse);
    }
    /**
     * POST endpoint to create a new status in the database
     * @param status the status object to be created
     * @return the created status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Status insertNewStatus(@RequestBody Status status) throws RecordAlreadyExistsException {
        logger.info("creating status...");
        Status insertedStatus = statusService.addStatus(status);
        logger.info("location with id: " + insertedStatus.getId()+" successfully created!");
        return insertedStatus;
    }
    /**
     * PUT endpoint to update a specific status by ID in the database
     * @param id the id of the status that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyStatusById(@RequestBody Status status, @PathVariable long id) {
        logger.info("updating status with id: " + id+"...");
        statusService.modifyStatusById(status, id);
        logger.info("status with id: " + id + " updated!");
    }
    /**
     * DELETE endpoint to delete an existing status in the database
     * @param id the id of the specific status to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStatusById(@PathVariable("id") long id) {
        logger.info("deleting status with id " + id+"...");
        statusService.deleteStatusById(id);
        logger.info("status with id "+ id +" successfully deleted!");
    }
}

