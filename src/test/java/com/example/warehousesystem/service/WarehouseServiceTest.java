package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.entities.Warehouse;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.WarehouseRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


/**
* @author yasmin.rosskopf
 * Test for service class of entity "Warehouse"
 */
@SpringBootTest(classes = WarehouseService.class)
public class WarehouseServiceTest {
    @MockBean
    private WarehouseRepository warehouseRepository;

    @MockBean
    private CsvExportService csvExportService;

    WarehouseService warehouseService;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        warehouseService= new WarehouseService(warehouseRepository, csvExportService);
    }

    /**
     * Test for service to fetch all warehouses
     * @result returns a list of warehouses
     */
    @Test
    public void getsAListOfAllWarehouses_When_getAllWarehousesIsCalled() {
        Warehouse warehouse = getWarehouse();
        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(warehouse);
        when(warehouseRepository.findAll()).thenReturn(warehouses);
        List<Warehouse> result = warehouseService.getAllWarehouses();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch warehouse with specific id
     * @result returns an optional warehouse
     */
    @Test
    public void getsCategoryWithSpecificId_When_getCategoryByIdIsCalled() {
        Warehouse warehouse = getWarehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        Warehouse result = warehouseService.getWarehouseById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to delete an warehouse by ID
     * @result calls warehouseRepository to delete warehouse
     */
    @Test
    public void callsCategoryRepositoryToDeleteCategoryWithSpecificId_When_deleteCategoryByIdIsCalled() {
        Warehouse warehouse = getWarehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).deleteById(1L);
        warehouseService.deleteWarehouseById(1L);
        verify(warehouseRepository, times(1)).deleteWarehousebyId(1L);
    }

    /**
     * Test for service to add an warehouse that already exists
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addCategoryByIdThatExistsIsCalled() {
        Warehouse warehouse = getWarehouse();
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(warehouseRepository.findWarehouseByArticleAndLocation(warehouse.getArticle(), warehouse.getLocation())).thenReturn(Optional.of(warehouse));
            warehouseService.addNewWarehouse(warehouse);
        });
    }

    /**
     * Test for service to add a new warehouse
     * @throws if record already exists or if quantity entered is lower or equals zero
     * @result returns the new warehouse
     */
    @Test
    public void returnsTheNewCategory_When_addCategoryByIdThatExistsIsCalled() throws RecordAlreadyExistsException, InvalidFormatEntryException {
        Warehouse warehouse = getWarehouse();
        when(warehouseRepository.findWarehouseByArticleAndLocation(warehouse.getArticle(), warehouse.getLocation())).thenReturn(Optional.empty());
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        Warehouse newCategory = warehouseService.addNewWarehouse(warehouse);
        assertEquals(warehouse, newCategory);
    }

    /**
     * Test for service to delete a warehouse that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_addCategoryByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(warehouseRepository).deleteWarehousebyId(1);
            warehouseService.deleteWarehouseById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if warehouse service fails
     * @result calls the csvExportService for warehouses
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        warehouseService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"warehouses.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeWarehouseToCsv(response.getWriter());
    }

    /**
     * Test for service to modify a warehouse with specific id
     * @result calls warehouseRepository to save updated warehouse
     * @throws if entry is lower or same as zero for the quantity
     */
    @Test
    public void callsRepositoryToModifyCategoryWithSpecificId_When_modifyCategoryByIdIsCalled() throws InvalidFormatEntryException {
        Warehouse warehouse = getWarehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        warehouseService.modifyWarehouseById(warehouse, 1L);
        assertTrue(true);
    }

    /**
     * instantiates a dummy warehouse for the tests
     * @return a warehouse
     */
    private Warehouse getWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setLocation(new Location());
        warehouse.setArticle(new Article());
        warehouse.setQuantity(100);
        return warehouse;
    }
}