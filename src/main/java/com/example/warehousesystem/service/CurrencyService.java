package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Currency;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.CurrencyRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author yasmin.rosskopf
 * Service class for the currency controller
 */
@Service
@AllArgsConstructor
public class CurrencyService implements HasLogger {
    private final Logger logger = getLogger();
    private final CurrencyRepository currencyRepository;

    private final CsvExportService csvExportService;

    /**
     * Gets a list of all currencies
     * @return list of all currencies
     */
    public List<Currency> getAllCurrencies(){
        return currencyRepository.findAll();
    }

    /**
     * Gets a specific currency by id
     * @param id id of the currency
     * @return currency with provided id
     */
    public Currency getCurrencyById(long id){
        return currencyRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Currency does not exist with id: " + id));
    }
    /**
     * Saves currencies to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException file related issues
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"currencies.csv\"");
        csvExportService.writeCurrenciesToCsv(servletResponse.getWriter());
    }

    /**
     * Adds a new currency to database
     * @param currency currency to be added
     * @return added currency
     * @throws RecordAlreadyExistsException if currency already exists in database
     */
    public Currency addCurrency(Currency currency) throws RecordAlreadyExistsException {
        Optional<Currency> existingCurrency = currencyRepository.
                findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(),currency.getCountry());
        if(existingCurrency.isPresent()){
            throw new RecordAlreadyExistsException("Currency already exists in database!");
        }
        currencyRepository.save(currency);
        return currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(),currency.getCountry()).get();

    }

    /**
     * Modifies a currency
     * @param currency modified currency object
     * @param id id of the currency to be modified
     */
    public void modifyCurrencyById(Currency currency, Long id) {
        Currency updatedCurrency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency does not exist with id: " + id));

        updatedCurrency.setCurrencyCode(currency.getCurrencyCode());
        updatedCurrency.setCountry(currency.getCountry());
        updatedCurrency.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("currency with id "+ id+" found and updating...");
        currencyRepository.save(updatedCurrency);
    }

    /**
     * deletes a currency with provided id
     * @param id id of the currency to be deleted
     */
    public void deleteCurrencyById(long id){
        currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency does not exist with id: " + id));
        logger.info("currency with id "+ id+" found and deleting...");
        currencyRepository.deleteById(id);
    }

}
