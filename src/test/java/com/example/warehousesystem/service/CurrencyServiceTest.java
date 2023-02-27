package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Currency;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.CurrencyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.warehousesystem.utils.TestHelperMethods.createDummyCurrency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


/**
 * @author yasmin.rosskopf
 * Test for service class of entity "Currency"
 */
@SpringBootTest(classes = CurrencyService.class)
public class CurrencyServiceTest {
    @MockBean
    private CurrencyRepository currencyRepository;
    @MockBean
    private CsvExportService csvExportService;
    private CurrencyService currencyService;
    private Currency currency;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = new CurrencyService(currencyRepository, csvExportService);
        currency = createDummyCurrency();
    }


    /**
     * Test for service to fetch all currencies
     * @result returns a list of currencies
     */
    @Test
    public void getsListOfAllCategories_When_getAllCategoriesIsCalled() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(currency);
        when(currencyRepository.findAll()).thenReturn(currencies);
        List<Currency> result = currencyService.getAllCurrencies();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch currency with specific id
     * @result returns an optional currency
     */
    @Test
    public void getsCurrencyWithSpecificId_When_getCurrencyByIdIsCalled() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        Currency result = currencyService.getCurrencyById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to add an currency that already exists
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addCurrencyByIdThatExistsIsCalled() {
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(), currency.getCountry())).thenReturn(Optional.of(currency));
            currencyService.addCurrency(currency);
        });
    }

    /**
     * Test for service to add a new currency
     * @result returns the new currency
     */
    @Test
    public void returnsTheNewCurrency_When_addCurrencyByIdThatExistsIsCalled() throws RecordAlreadyExistsException {
        when(currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(), currency.getCountry())).thenReturn(Optional.empty()).thenReturn(Optional.of(currency));
        when(currencyRepository.save(currency)).thenReturn(currency);
        Currency newCurrency = currencyService.addCurrency(currency);
        assertEquals(currency, newCurrency);
    }

    /**
     * Test for service to delete an currency by ID
     * @result calls currencyRepository to delete currency
     */
    @Test
    public void callsCurrencyRepositoryToDeleteCurrencyWithSpecificId_When_deleteCurrencyByIdIsCalled() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        doNothing().when(currencyRepository).deleteById(1L);
        currencyService.deleteCurrencyById(1L);
        verify(currencyRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for service to delete an currency that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_addCurrencyByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(currencyRepository).deleteById(1L);
            currencyService.deleteCurrencyById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if currency service fails
     * @result calls the csvExportService for currencies
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        currencyService.getCsv(response1);
        assertEquals("text/csv", response1.getContentType());
        assertEquals("attachment; filename=\"currencies.csv\"", response1.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeCurrenciesToCsv(response1.getWriter());
    }


    /**
     * Test for service to modify a currency with specific id
     * @result calls currencyRepository to save updated currency
     */
    @Test
    public void callsRepositoryToModifyCurrencyWithSpecificId_When_modifyCurrencyByIdIsCalled() {
        Currency modifiedCurrency = new Currency();
        modifiedCurrency.setId(2L);
        modifiedCurrency.setCurrencyCode("CHF");
        modifiedCurrency.setCountry("Switzerland");

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.save(modifiedCurrency)).thenReturn(modifiedCurrency);

        assertNotEquals(currency, modifiedCurrency);
        currencyService.modifyCurrencyById(modifiedCurrency, 1L);

        assertEquals(currency, modifiedCurrency);
        verify(currencyRepository, times(1)).save(currency);
    }

}