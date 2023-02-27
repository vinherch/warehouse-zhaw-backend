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
 * Defines a Bean for the dev-Profile
 * @author dejan.kosic
**/
@Configuration
@Profile("dev")
@Transactional
public class DevConfiguration implements HasLogger {
    private Logger logger = getLogger();
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;


    public DevConfiguration() {
        logger.info("Dev Configuration Class");
    }

    /**
     * Creates initial data for all entities
     */
    @PostConstruct
    public void createData() {
        createStatusData();
        createCategory();
        createCurrency();
        createLocation();
        createArticle();
        createWarehouse();
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

        Currency c1 = new Currency("EUR", "Deutschland");
        saveCurrency(c1);

        Currency c2 = new Currency("USD", "USA"
        );
        saveCurrency(c2);

        Currency c3 = new Currency("CAD", "Canada");
        saveCurrency(c3);

    }
    /**
     * Creates initial data for the entity location
     */
    private void createLocation() {
        Location c1 = new Location("A", 1, 1);
        saveLocation(c1);

        Location c2 = new Location("A", 1, 2);
        saveLocation(c2);

        Location c3 = new Location("A", 2, 1);
        saveLocation(c3);

        Location c4 = new Location("B", 1,1);
        saveLocation(c4);
    }
    /**
     * Creates initial data for the entity category
     */
    public void createCategory() {
        Category defaultCategory = new Category(
                "Standard"
        );
        saveCategory(defaultCategory);

        Category category1 = new Category(
                "Schuhe"
        );
        saveCategory(category1);

        Category category2 = new Category(
                "Sport"
        );
        saveCategory(category2);

        Category category3 = new Category(
                "Hemden"
        );
        saveCategory(category3);

        Category category4 = new Category(
                "Röcke"
        );
        saveCategory(category4);

    }
    /**
     * Creates initial data for the entity article
     */
    public void createArticle() {
        Article article1 = new Article();
        String articleName = "Grüner Maxirock";
        article1.setDescription(articleName);
        article1.setAmount(79.9);
        article1.setCurrency(currencyRepository.findCurrencyByCurrencyCodeAndCountry("CHF", "Schweiz").get());
        article1.setStatus(statusRepository.findStatusByDescription("ACTIVE").get());
        article1.setCategory(categoryRepository.findCategoryByDescription("Röcke").get());
        saveArticle(article1);



    }
    /**
     * Creates initial data for the entity warehouse
     */
    public void createWarehouse() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setArticle(articleRepository.findArticleByDescription("Grüner Maxirock").get());
        warehouse1.setQuantity(200);
        warehouse1.setLocation(locationRepository.findLocationByAisleAndShelfAndTray("A", 1, 1).get());
        saveWarehouse(warehouse1);
    }

    /**
     * saves an aricle entity into database if not already existing
     * @param article
     */
    public void saveArticle(Article article){
        if(articleRepository.findArticleByDescription(article.getDescription()).isEmpty()){
            articleRepository.save(article);
        }
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
     * saves a location entity into database if not already existing
     * @param location location to be saved
     */
    public void saveLocation(Location location){
        if(locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(),location.getShelf(),location.getTray()).isEmpty()){
            locationRepository.save(location);
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

    /**
     * saves a warehouse entity into database if not already existing
     * @param warehouse warehouse to be saved
     */
    public void saveWarehouse(Warehouse warehouse){
        if (warehouseRepository.findWarehouseByArticleAndLocation(warehouse.getArticle(), warehouse.getLocation()).isEmpty()){
            warehouseRepository.save(warehouse);
        }
    }


}
