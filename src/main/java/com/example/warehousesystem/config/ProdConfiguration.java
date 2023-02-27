package com.example.warehousesystem.config;

import com.example.warehousesystem.entities.*;
import com.example.warehousesystem.repository.*;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author dejan.kosic
 * Defines a Bean for the Prod-Profile
 **/
@Configuration
@Profile("prod")
@Transactional
public class ProdConfiguration implements HasLogger {
    private Logger logger = getLogger();
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CurrencyRepository currencyRepository;


    public ProdConfiguration() {
        logger.info("Prod Configuration Class");
    }

    /**
     * Creates initial data for all entities
     */
    @PostConstruct
    public void createData() {
        createStatusData();
        createCategory();
        createCurrency();
    }

    /**
     * Creates initial data for the entity status
     */
    private void createStatusData() {
        Status defaultStatus = new Status("CREATED");
        saveStatus(defaultStatus);
        Status s1 = new Status("ACTIVE"
        );
        saveStatus(s1);

        Status s2 = new Status("INACTIVE"
        );
        saveStatus(s2);

    }
    /**
     * Creates initial data for the entity currency
     */
    private void createCurrency() {
        Currency defaultCurrency = new Currency("CHF", "Schweiz");
        saveCurrency(defaultCurrency);

    }

    /**
     * Creates initial data for the entity category
     */
    public void createCategory() {
        Category defaultCategory = new Category(
                "Standard"
        );
        saveCategory(defaultCategory);

    }


    /**
     * saves a category entity into database if not already existing
     * @param category category to be saved
     */
    public void saveCategory(Category category){
        if(categoryRepository.findCategoryByDescription(category.getDescription()).isEmpty()) {
            categoryRepository.save(category);
        }

    }
    /**
     * saves a status entity into database if not already existing
     * @param status status to be saved
     */
    public void saveStatus(Status status){
        if(statusRepository.findStatusByDescription(status.getDescription()).isEmpty()){
            statusRepository.save(status);
        }
    }
    /**
     * saves a currency entity into database if not already existing
     * @param currency currency to be saved
     */
    public void saveCurrency(Currency currency){
        if(currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(),currency.getCountry()).isEmpty()) {
            currencyRepository.save(currency);
        }
    }



}

