package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.*;
import com.example.warehousesystem.service.*;
import com.example.warehousesystem.utils.BarcodeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.example.warehousesystem.utils.TestHelperMethods.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dejan.kosic
 *  Test for controller class for entity barcode mapping
 */
@WebMvcTest(BarcodeMappingController.class)
public class BarcodeMappingControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BarcodeMappingService barcodeMappingService;

    @MockBean
    private ArticleService articleService;
    @MockBean
    private StatusService statusService;
    @MockBean
    private CurrencyService currencyService;
    @MockBean
    private CategoryService categoryService;

    /**
     * Tests the endpoint for retrieving all barcode mappings.
     * @result Verifies that the response has a status of OK and contains
     * a list of barcode mappings with the expected values.
     * @throws Exception if any error occurs during the test.
     */
    @Test
    public void testGetAllBarcodeMappings() throws Exception {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        List<BarcodeMapping> barcodeMappingList = new ArrayList<>();
        barcodeMappingList.add(barcodeMapping);
        when(barcodeMappingService.getAllBarcodeMappings()).thenReturn(barcodeMappingList);
        mvc.perform(get("/v1/barcodemappings")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ean", is(barcodeMapping.getEan())))
                .andExpect(jsonPath("$[0].description", is(barcodeMapping.getDescription())));
    }
    /**
     * Tests the endpoint for retrieving a barcode mapping by its ID.
     * @result barcode mappings with the expected values to the ID provided
     * @throws Exception if any error occurs during the test.
     */

    @Test
    public void testGetBarcodeMappingById() throws Exception {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        when(barcodeMappingService.getBarcodeMappingById(1)).thenReturn(barcodeMapping);
        mvc.perform(get("/v1/barcodemappings/" + barcodeMapping.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ean", is(barcodeMapping.getEan())))
                .andExpect(jsonPath("$.description", is(barcodeMapping.getDescription())));
    }
    /**
     * Tests the endpoint for deleting a barcode mapping by its ID.
     * @result that te status is NO_CONTENT and that the barcode mapping with provided id is deleted
     * @throws Exception if any error occurs during the test.
     */
    @Test
    public void testDeleteBarcodeMappingById() throws Exception {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        doNothing().when(barcodeMappingService).deleteBarcodeMappingById(1L);
        mvc.perform(delete("/v1/barcodemappings/" + barcodeMapping.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }

    /**
     * Tests the functionality of adding a barcode mapping endpoint.
     * @result response has a status of CREATED.
     * @throws Exception if the request or response processing fails.
     */
    @Test
    public void testAddBarcodeMapping() throws Exception {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        when(barcodeMappingService.addNewBarcodeMapping(barcodeMapping)).thenReturn(barcodeMapping);
        mvc.perform(post("/v1/barcodemappings").content(asJson(barcodeMapping)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }
    /**
     * Tests the functionality of updating a barcode mapping endpoint.
     * @result  the response has a status of OK.
     * @throws Exception in case of any unexpected errors
     */
    @Test
    public void testUpdateBarcodeMapping() throws Exception {
        BarcodeMapping barcodeMapping = getBarcodeMapping();
        doNothing().when(barcodeMappingService).modifyBarcodeMappingById(barcodeMapping, 1L);
        mvc.perform(put("/v1/barcodemappings/" + barcodeMapping.getId())
                        .content(asJson(barcodeMapping))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    /**
     * Tests the functionality of scanning a new barcode endpoint.
     * @result The response has a status of CREATED and getBarcodeMappingByEan
     * and addArticle methods have been called once.
     */
    @Test
    public void testScanNewArticle() throws Exception {
        BarcodeHelper barcodeHelper = new BarcodeHelper("123456789");

        BarcodeMapping foundBarcodeMapping = new BarcodeMapping();
        foundBarcodeMapping.setEan("123456789");
        foundBarcodeMapping.setDescription("Test Article");

        Currency currency = createDummyCurrency();
        Category category = createDummyCategory();
        Status status = createDummyStatus();

        Article article = new Article("Test Article",category,currency,status,1.00);
        when(barcodeMappingService.getBarcodeMappingByEan(barcodeHelper.getBarcodeNumber())).thenReturn(foundBarcodeMapping);
        when(currencyService.getCurrencyById(1)).thenReturn(currency);
        when(categoryService.getCategoryById(1)).thenReturn(category);
        when(statusService.getStatusById(1)).thenReturn(status);
        when(articleService.addArticle(article)).thenReturn(article);

        mvc.perform(post("/v1/barcodemappings/scan")
                        .contentType(APPLICATION_JSON)
                        .content(asJson(barcodeHelper)))
                .andExpect(status().isCreated());

        verify(barcodeMappingService, times(1)).getBarcodeMappingByEan("123456789");
        verify(articleService, times(1)).addArticle(article);
    }
    /**
     * Returns a BarcodeMapping object with pre-defined values for testing purposes.
     * @return barcode mapping object with values for id, description, and ean set.
     */
    private BarcodeMapping getBarcodeMapping() {
        BarcodeMapping barcodeMapping = new BarcodeMapping();
        barcodeMapping.setId(1L);
        barcodeMapping.setDescription("Velo");
        barcodeMapping.setEan("7888883484856");
        return barcodeMapping;
    }

}
