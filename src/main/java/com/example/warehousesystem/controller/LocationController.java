package com.example.warehousesystem.controller;


import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.LocationService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author yasmin.rosskopf
 * Controller class that provides CRUD-operation endpoints in regard to the location entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 */

@RestController
@RequestMapping("/v1/locations")
public class LocationController implements HasLogger {

    private final Logger logger = getLogger();
    private final LocationService locationService;

    /**
     * Custom constructor for the location controller
     * @param locationService service for the location controller
     */
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * GET endpoint to fetch all locations in the database
     * @return a list of all locations
     */
    @GetMapping
    public List<Location> getAllLocations() {
        logger.info("returns all locations");
        return this.locationService.getAllLocations();
    }

    /**
     * GET endpoint to fetch a specific location by ID in the database
     * @param id the id of the location that is to be found
     * @return the specific location
     */
    @GetMapping("/{id}")
    public Location getLocationById(
            @PathVariable Long id
    ) {logger.info("get location with id "  + id);
       return this.locationService.getLocationById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all locations in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getLocationsAsCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with locations");
        this.locationService.getCsv(servletResponse);
    }


    /**
     * POST endpoint to create a new location in the database
     * @param location the location object to be created
     * @return the created location
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Location addLocation(@RequestBody Location location) throws RecordAlreadyExistsException {
        logger.info("creating location...");
        Location insertedLocation = locationService.addLocation(location);
        logger.info("location with id: " + insertedLocation.getId()+" successfully created!");
        return insertedLocation;
    }

    /**
     * PUT endpoint to update a specific location by ID in the database
     * @param id the id of the location that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyLocationById(@RequestBody Location location, @PathVariable long id){
        logger.info("updating location with id: " + id+"...");
        locationService.modifyLocationById(location,id);
        logger.info("location with id: " + id + " updated!");
    }

    /**
     * DELETE endpoint to delete an existing location in the database
     * @param id the id of the specific location to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(
            @PathVariable Long id
    ) {
        logger.info("deleting location with id "  + id+"...");
        locationService.deleteLocationById(id);
        logger.info("location with id "+ id +" successfully deleted!");

    }
}