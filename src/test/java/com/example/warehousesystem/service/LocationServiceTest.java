package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.LocationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


/**
 * @author yasmin.rosskopf
 * Test for service class of entity "Location"
 */
@SpringBootTest(classes = LocationService.class)
public class LocationServiceTest {
    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private CsvExportService csvExportService;

    LocationService locationService;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        locationService = new LocationService(locationRepository, csvExportService);
    }

    /**
     * Test for service to fetch all locations
     * @result returns a list of locations
     */
    @Test
    public void getsAListOfAllLocations_When_getAllLocationsIsCalled() {
        Location location = getLocation();
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        when(locationRepository.findAll()).thenReturn(locations);
        List<Location> result = locationService.getAllLocations();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch all locations
     * @result returns an empty list of locations
     */
    @Test
    public void returnsEmptyList_When_getAllLocationsIsCalled() {
        List<Location> locations = new ArrayList<>();
        when(locationRepository.findAll()).thenReturn(locations);
        List<Location> result = locationService.getAllLocations();
        assertEquals(result.size(), 0);
    }

    /**
     * Test for service to fetch location with specific id
     * @result returns an optional location
     */
    @Test
    public void getsLocationWithSpecificId_When_getLocationByIdIsCalled() {
        Location location = getLocation();
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        Location result = locationService.getLocationById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to add a location that already exists
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addLocationByIdThatExistsIsCalled() {
        Location location = getLocation();
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), location.getTray())).thenReturn(Optional.of(location));
            locationService.addLocation(location);
        });
    }

    /**
     * Test for service to add a new location
     * @result returns the new location
     */
    @Test
    public void returnsTheNewLocation_When_addLocationByIdThatExistsIsCalled() throws RecordAlreadyExistsException {
        Location location = getLocation();
        when(locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), location.getTray()))
                .thenReturn(Optional.empty()).thenReturn(Optional.of(location));
        when(locationRepository.save(location)).thenReturn(location);
        Location newLocation = locationService.addLocation(location);
        assertEquals(location, newLocation);
    }

    /**
     * Test for service to delete an location by ID
     * @result calls locationRepository to delete location
     */
    @Test
    public void callsLocationRepositoryToDeleteLocationWithSpecificId_When_deleteLocationByIdIsCalled() {

        Location location = getLocation();
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        doNothing().when(locationRepository).deleteById(1L);
        locationService.deleteLocationById(1L);
        verify(locationRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if locations service fails
     * @result calls the csvExportService for locations
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        locationService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"locations.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeLocationsToCsv(response.getWriter());
    }

    /**
     * Test for service to delete a location that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_deleteLocationByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(locationRepository).deleteById(1L);
            locationService.deleteLocationById(1L);
        });
    }

    /**
     * Test for service to modify a location with specific id
     * @result calls locationRepository to save updated location
     */
    @Test
    public void callsRepositoryToModifyLocationWithSpecificId_When_modifyLocationByIdIsCalled() {
        Location location = getLocation();
        Location modifiedLocation = new Location();
        modifiedLocation.setId(2L);
        modifiedLocation.setAisle("Z");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(modifiedLocation)).thenReturn(modifiedLocation);

        assertNotEquals(location, modifiedLocation);
        locationService.modifyLocationById(modifiedLocation, 1L);

        assertEquals(location, modifiedLocation);
        verify(locationRepository, times(1)).save(location);
    }

    /**
     * instantiates a dummy location for the tests
     * @return the new location
     */
    private Location getLocation() {
        Location location = new Location();
        location.setId(1L);
        location.setAisle("A");
        location.setTray(5);
        location.setShelf(100);
        return location;
    }
}