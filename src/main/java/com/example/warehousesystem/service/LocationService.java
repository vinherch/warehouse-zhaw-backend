package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.LocationRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author yasmin.rosskopf
 * Service class for the location controller
 */
@Service
@AllArgsConstructor
public class LocationService implements HasLogger {

    private final Logger logger = getLogger();
    private final LocationRepository locationRepository;
    private CsvExportService csvExportService;

    /**
     * Gets a list of all locations
     * @return list of all locations
     */
    public List<Location> getAllLocations(){
        return locationRepository.findAll();
    }

    /**
     * Gets a specific location by id
     * @param id id of the location
     * @return location with provided id
     */
    public Location getLocationById(long id) {
        return locationRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Location does not exist with id: " + id));
    }
    /**
     * Adds a new location to database
     * @param location location to be added
     * @return added location
     * @throws RecordAlreadyExistsException if location already exists in database
     */
    public Location addLocation(Location location) throws RecordAlreadyExistsException {
        Optional<Location>existingLocation = locationRepository.
                findLocationByAisleAndShelfAndTray(location.getAisle(),location.getShelf(), location.getTray());
        if(existingLocation.isPresent()){
            throw new RecordAlreadyExistsException("Location already exists in database!");
        }
        locationRepository.save(location);
        return locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), location.getTray()).get();
    }
    /**
     * Modifies a location
     * @param location modified location object
     * @param id id of the location to be modified
     */
    public void modifyLocationById(Location location, Long id) {
        Location updatedLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location does not exist with id: " + id));

        updatedLocation.setAisle(location.getAisle());
        updatedLocation.setShelf(location.getShelf());
        updatedLocation.setTray(location.getTray());
        updatedLocation.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("location with id "+ id+" found and updating...");
        locationRepository.save(updatedLocation);
    }

    /**
     * deletes a location with provided id
     * @param id id of the location to be deleted
     */
    public void deleteLocationById(long id){
        locationRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Location does not exist with id: " + id));
        logger.info("location with id "+ id+" found and deleting...");
        locationRepository.deleteById(id);
    }
    
    /**
     * Saves locations to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"locations.csv\"");
        csvExportService.writeLocationsToCsv(servletResponse.getWriter());
    }

}
