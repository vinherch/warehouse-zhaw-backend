package com.example.warehousesystem.controller;


import com.example.warehousesystem.entities.Currency;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.CSVImportService;
import com.example.warehousesystem.service.CurrencyService;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author yasmin.rosskopf
 * Controller class that provides CRUD-operation endpoints in regard to the currency entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 */

@RestController
@RequestMapping("/v1/currencies")
public class CurrencyController implements HasLogger {

    private final Logger logger = getLogger();
    private final CurrencyService currencyService;
    private final CSVImportService fileService;

    /**
     * Custom constructor for the currency controller
     *
     * @param currencyService service of the currency controller
     * @param fileService file service for the currency import
     */
    public CurrencyController(CurrencyService currencyService, CSVImportService fileService) {
        this.currencyService = currencyService;
        this.fileService = fileService;
    }

    /**
     * GET endpoint to fetch all currencies in the database
     * @return a list of all currencies
     */
    @GetMapping
    public List<Currency> getAllCurrencies() {
        logger.info("returns all currencies");
        return this.currencyService.getAllCurrencies();
    }

    /**
     * GET endpoint to fetch a specific currency by ID in the database
     * @param id the id of the currency that is to be found
     * @return the specific currency
     */
    @GetMapping("/{id}")
    public Currency getCurrencyById(
            @PathVariable Long id
    ) {
        logger.info("get currency with id "  + id);
       return this.currencyService.getCurrencyById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all currencies in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getAllCurrenciesInCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with currencies");
        this.currencyService.getCsv(servletResponse);
    }


    /**
     * POST endpoint to create a new currency in the database
     * @param currency the currency object to be created
     * @return the created currency
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Currency addCurrency(@RequestBody Currency currency) throws RecordAlreadyExistsException {
        logger.info("creating currency...");
        Currency insertedCurrency = currencyService.addCurrency(currency);
        logger.info("currency with id: " + insertedCurrency.getId()+" successfully created!");
        return insertedCurrency;
    }


    /**
     * PUT endpoint to update a specific currency by ID in the database
     * @param id the id of the currency that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyCurrencyById(@RequestBody Currency currency, @PathVariable long id){
        logger.info("updating currency with id: " + id+"...");
        currencyService.modifyCurrencyById(currency,id);
        logger.info("currency with id: " + id + " updated!");
    }

    /**
     * DELETE endpoint to delete an existing currency in the database
     * @param id the id of the specific currency to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrency(
            @PathVariable Long id
    ) {
        logger.info("deleting currency with id "  + id+"...");
        currencyService.deleteCurrencyById(id);
        logger.info("currency with id "+ id +" successfully deleted!");
    }
}