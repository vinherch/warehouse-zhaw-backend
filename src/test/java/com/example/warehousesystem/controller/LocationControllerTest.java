package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.warehousesystem.utils.TestHelperMethods.asJson;
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyLocation;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yasmin.rosskopf
 * Test for controller class for entity "location"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 */
@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private LocationService locationService;

    private Location location;

    /**
     * instantiates up a dummy Location to test on before each test
     */
    @BeforeEach
    public void setup() {
        location = createDummyLocation();
    }

    /**
     * Test for GET endpoint to fetch all locations
     * @result returns a response with 200 OK, body with list of locations
     */
    @Test
    public void getsResponseWithListOfAllCurrencies_When_getAllCurrenciesIsCalled() throws Exception {
        List<Location> LocationList = new ArrayList<>();
        LocationList.add(location);
        when(locationService.getAllLocations()).thenReturn(LocationList);
        mvc.perform(get("/v1/locations")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].aisle", is(location.getAisle())));
    }

    /**
     * Test for GET endpoint to an location by ID
     * @result returns a response with 200 OK, body with location with specific id
     */
    @Test
    public void getsResponseWithLocationWithSpecificId_When_getLocationByIdIsCalled() throws Exception {
        when(locationService.getLocationById(1)).thenReturn(location);
        mvc.perform(get("/v1/locations/" + location.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aisle", is(location.getAisle())));
    }

    /**
     * Test for DELETE endpoint to delete an location by ID
     * @result returns a response with 204 NO_CONTENT, and calls locationService to delete location
     */
    @Test
    public void getsResponseWithNoContent_When_deleteLocationByIdIsCalled() throws Exception {
        doNothing().when(locationService).deleteLocationById(1L);
        mvc.perform(delete("/v1/locations/" + location.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }

    /**
     * Test for POST endpoint to add a new location
     * @result returns a response with 201 CREATED and body with the new location
     */
    @Test
    public void getsResponseWithLocationWithId_When_addLocationIsCalled() throws Exception {
        when(locationService.addLocation(location)).thenReturn(location);
        mvc.perform(post("/v1/locations").content(asJson(location)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }


    /**
     * Test for POST endpoint to add a new location
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addlocationIsCalled() throws Exception {
        //when
        when(locationService.addLocation(location)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/locations")
                                .content(asJson(location))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }


    /**
     * Test for PUT endpoint to modify an existing location
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithLocationWithSpecificId_When_modifyLocationByIdIsCalled() throws Exception {
        doNothing().when(locationService).modifyLocationById(location, 1L);
        mvc.perform(put("/v1/locations/" + location.getId())
                        .content(asJson(location))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }
}
