package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Warehouse;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CSVImportService;
import com.example.warehousesystem.service.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.example.warehousesystem.utils.TestHelperMethods.asJson;
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyWarehouse;
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
 * Test for controller class for entity "Warehouse"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 */

@WebMvcTest(WarehouseController.class)
public class WareHouseControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private WarehouseService warehouseService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private CSVImportService fileService;
    private Warehouse warehouse;

    /**
     * instantiates up a dummy warehouse to test on before each test
     */
    @BeforeEach
    public void setup() {
        warehouse = createDummyWarehouse();
    }


    /**
     * Test for GET endpoint to fetch all warehouses
     * @result returns a response with 200 OK, body with list of warehouses
     */
    @Test
    public void testGetAllWarehouse() throws Exception {
       
        List<Warehouse> warehouseList = new ArrayList<>();
        warehouseList.add(warehouse);
        when(warehouseService.getAllWarehouses()).thenReturn(warehouseList);
        mvc.perform(get("/v1/warehouses")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity", is(warehouse.getQuantity())));
    }

    /**
     * Test for GET endpoint to a warehouse by ID
     * @result returns a response with 200 OK, body with warehouse with specific id
     */
    @Test
    public void testGetWarehouseById() throws Exception {
       
        when(warehouseService.getWarehouseById(1)).thenReturn(warehouse);
        mvc.perform(get("/v1/warehouses/" + warehouse.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(warehouse.getQuantity())));
    }

    /**
     * Test for DELETE endpoint to delete a warehouse by ID
     * @result returns a response with 204 NO_CONTENT, and calls warehouseService to delete warehouse
     */
    @Test
    public void testDeleteWarehouse() throws Exception {
       
        doNothing().when(warehouseService).deleteWarehouseById(1L);
        mvc.perform(delete("/v1/warehouses/" + warehouse.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }

    /**
     * Test for POST endpoint to add a new warehouse
     * @result returns a response with 201 CREATED and body with the new warehouse
     */
    @Test
    public void testAddWarehouse() throws Exception {
       
        when(warehouseService.addNewWarehouse(warehouse)).thenReturn(warehouse);
        mvc.perform(post("/v1/warehouses").content(asJson(warehouse)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }
    /**
     * Test for POST endpoint to add a new warehouse
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addwarehouseIsCalled() throws Exception {
        //when
        when(warehouseService.addNewWarehouse(warehouse)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/warehouses")
                                .content(asJson(warehouse))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }

    /**
     * Test for PUT endpoint to modify an existing warehouse
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithWarehouseWithSpecificId_When_modifyWarehouseByIdIsCalled() throws Exception {
       
        doNothing().when(warehouseService).modifyWarehouseById(warehouse, 1L);
        mvc.perform(put("/v1/warehouses/" + warehouse.getId()).content(asJson(warehouse)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    /**
     * Test for Upload a csv with warehouses
     * @result returns a response with 500 Bad Request
     */
    @Test
    public void returnsBadRequest_When_FileUploadedIsNotOfTypeCSV()
            throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.csv",
                MediaType.ALL_VALUE,
                "Hello, World!".getBytes()
        );

        MockMvc mockMvc
                = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/v1/warehouses/upload").file(file))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test for Upload a csv with warehouses
     * @result returns a response with 200 OK
     */
    @Test
    public void returnsOK_When_FileUploadedIsOfTypeCSV() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.csv",
                "text/csv",
                "Hello, World!".getBytes()
        );

        MockMvc mockMvc
                = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/v1/warehouses/upload").file(file))
                .andExpect(status().isOk());
    }


}
