package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.BarcodeMapping;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.BarcodeMappingRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * @author dejan.kosic
 * Test class for the BarcodeMappingService
 */
@SpringBootTest(classes = BarcodeMappingService.class)
public class BarcodeMappingServiceTest {
    @MockBean
    private BarcodeMappingRepository barcodeMappingRepository;

    @MockBean
    private CsvExportService csvExportService;

    private BarcodeMappingService barcodeMappingService;

    @BeforeEach
    void setUp() {
        barcodeMappingService= new BarcodeMappingService(barcodeMappingRepository, csvExportService);
    }

    @Test
    public void testGetAllBarcodeMappings() {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        List<BarcodeMapping> barcodeMappings= new ArrayList<>();
        barcodeMappings.add(barcodeMapping);
        when(barcodeMappingRepository.findAll()).thenReturn(barcodeMappings);
        List<BarcodeMapping> result = barcodeMappingService.getAllBarcodeMappings();
        assertEquals(result.size(), 1);
    }

    @Test
    public void testGetBarcodeMappingById() {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        when(barcodeMappingRepository.findById(1L)).thenReturn(Optional.of(barcodeMapping));
        BarcodeMapping result = barcodeMappingService.getBarcodeMappingById(1);
        assertEquals(result.getId(), 1);
    }

    @Test
    public void testGetBarcodeMappingByEan(){
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        when(barcodeMappingRepository.findBarcodeMappingByEan("7888883484856")).thenReturn(Optional.of(barcodeMapping));
        BarcodeMapping result = barcodeMappingService.getBarcodeMappingByEan("7888883484856");
        assertEquals(result.getEan(), "7888883484856");
    }

    @Test
    public void testAddBarcodeMapping_alreadyExists() {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(barcodeMappingRepository.findBarcodeMappingByEanAndDescription(barcodeMapping.getEan(),
                    barcodeMapping.getDescription())).thenReturn(Optional.of(barcodeMapping));
            barcodeMappingService.addNewBarcodeMapping(barcodeMapping);
        });
    }

    @Test
    public void testAddBarcodeMapping() throws RecordAlreadyExistsException {
        BarcodeMapping barcodeMapping= getBarcodeMapping();
        when(barcodeMappingRepository.findBarcodeMappingByEanAndDescription(barcodeMapping.getEan(), barcodeMapping.getDescription()))
                .thenReturn(Optional.empty()).thenReturn(Optional.of(barcodeMapping));
        when(barcodeMappingRepository.save(barcodeMapping))
                .thenReturn(barcodeMapping);
        barcodeMappingService.addNewBarcodeMapping(barcodeMapping);
        assertTrue(true);
    }

    @Test
    public void testDeleteBarcodeMapping() {
        BarcodeMapping barcodeMapping= getBarcodeMapping();
        when(barcodeMappingRepository.findById(1L)).thenReturn(Optional.of(barcodeMapping));
        doNothing().when(barcodeMappingRepository).deleteById(1L);
        barcodeMappingService.deleteBarcodeMappingById(1L);
        assertTrue(true);
    }

    @Test
    public void testDeleteBarcodeMapping_throwsResourceNotFoundException() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(barcodeMappingRepository).deleteById(1L);
            barcodeMappingService.deleteBarcodeMappingById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if barcode mapping service fails
     * @result calls the csvExportService for barcodes
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        barcodeMappingService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"BarcodeMapping.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeBarcodeMappingToCsv(response.getWriter());
    }

    @Test
    public void testSaveOrUpdateBarcodeMapping() {
        BarcodeMapping barcodeMapping= getBarcodeMapping();
        BarcodeMapping modifiedBarcodeMapping = getBarcodeMapping();
        modifiedBarcodeMapping.setEan("849477544444");
        modifiedBarcodeMapping.setDescription("Test");

        when(barcodeMappingRepository.findById(1L)).thenReturn(Optional.of(barcodeMapping));
        when(barcodeMappingRepository.save(modifiedBarcodeMapping)).thenReturn(modifiedBarcodeMapping);

        assertNotEquals(barcodeMapping, modifiedBarcodeMapping);
        barcodeMappingService.modifyBarcodeMappingById(modifiedBarcodeMapping, 1L);

        assertEquals(barcodeMapping, modifiedBarcodeMapping);
        verify(barcodeMappingRepository, times(1)).save(barcodeMapping);

    }


    private BarcodeMapping getBarcodeMapping() {
        BarcodeMapping barcodeMapping = new BarcodeMapping();
        barcodeMapping.setId(1L);
        barcodeMapping.setDescription("Velo");
        barcodeMapping.setEan("7888883484856");
        return barcodeMapping;
    }
}
