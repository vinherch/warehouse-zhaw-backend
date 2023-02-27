package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Currency;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CSVImportService;
import com.example.warehousesystem.service.CurrencyService;
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
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyCurrency;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dejan.kosic
 * Test for controller class of entity "Currency"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 **/

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CurrencyService currencyService;
    @MockBean
    private CSVImportService fileService;
    private Currency currency;

    /**
     * instantiates up a dummy currency to test on before each test
     */
    @BeforeEach
    public void setup() {
        currency = createDummyCurrency();
    }

    /**
     * Test for GET endpoint to fetch all currencies
     * @result returns a response with 200 OK, body with list of currencies
     */
    @Test
    public void getsResponseWithListOfAllCurrencies_When_getAllCurrenciesIsCalled() throws Exception {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(currency);
        when(currencyService.getAllCurrencies()).thenReturn(currencyList);
        mvc.perform(get("/v1/currencies")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyCode", is(currency.getCurrencyCode())));
    }

    /**
     * Test for GET endpoint to a currency by ID
     * @result returns a response with 200 OK, body with currency with specific id
     */
    @Test
    public void getsResponseWithCurrencyWithSpecificId_When_getCurrencyByIdIsCalled()throws Exception {
        when(currencyService.getCurrencyById(1)).thenReturn(currency);
        mvc.perform(get("/v1/currencies/" + currency.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode", is(currency.getCurrencyCode())));
    }


    /**
     * Test for DELETE endpoint to delete an currency by ID
     * @result returns a response with 204 NO_CONTENT, and calls currencyService to delete currency
     */
    @Test
    public void getsResponseWithNoContent_When_deleteCurrencyByIdIsCalled() throws Exception {
        doNothing().when(currencyService).deleteCurrencyById(1L);
        mvc.perform(delete("/v1/currencies/" + currency.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }


    /**
     * Test for POST endpoint to add a new currency
     * @result returns a response with 201 CREATED and body with the new currency
     */
    @Test
    public void getsResponseWithCurrencyWithId_When_addCurrencyIsCalled() throws Exception {
        when(currencyService.addCurrency(currency)).thenReturn(currency);
        mvc.perform(post("/v1/currencies").content(asJson(currency)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }

    /**
     * Test for POST endpoint to add a new currency
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addCurrencyIsCalled() throws Exception {
        //when
        when(currencyService.addCurrency(currency)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/currencies")
                                .content(asJson(currency))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }

    /**
     * Test for PUT endpoint to modify an existing currency
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithCurrencyWithSpecificId_When_modifyCurrencyByIdIsCalled() throws Exception {
        doNothing().when(currencyService).modifyCurrencyById(currency, 1L);
        mvc.perform(put("/v1/currencies/" + currency.getId())
                        .content(asJson(currency))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }
}