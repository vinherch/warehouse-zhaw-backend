package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.*;
import com.example.warehousesystem.repository.*;
import com.example.warehousesystem.utils.HasLogger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
/**
 * @author yasmin.rosskopf
 * This service persists the content of a csv File to the database
 */
@Service
public class CSVImportService implements HasLogger {
    private final Logger log = getLogger();
    final CurrencyRepository currencyRepository;
    final WarehouseRepository warehouseRepository;
    final CategoryRepository categoryRepository;
    final LocationRepository locationRepository;
    final StatusRepository statusRepository;
    final ArticleRepository articleRepository;

    /**
     * Custom constructor for the StatusController
     * @param currencyRepository repository of the entity currency
     * @param warehouseRepository repository of the entity warehouse
     * @param categoryRepository repository of the entity category
     * @param locationRepository repository of the entity location
     * @param statusRepository repository of the entity status
     * @param articleRepository repository of the entity article
     */
    public CSVImportService(CurrencyRepository currencyRepository, WarehouseRepository warehouseRepository, CategoryRepository categoryRepository, LocationRepository locationRepository, StatusRepository statusRepository, ArticleRepository articleRepository) {
        this.currencyRepository = currencyRepository;
        this.warehouseRepository = warehouseRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.statusRepository = statusRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * loops through all rows in the csv file and orchestrates the instantiation of all objects
     * @param file the csv file that was uploaded
     */
    public void saveAllEntitiesToDBFromCSV(MultipartFile file) {
        log.info("starts importing csv ");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            // iterates through every single line in the csv file
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            // for every csvRecord (one row in the csv-file) this extracts new objects for currency, category, status and article
            // and saves it into the DB. If the object already exists, the object gets updated.
            for (CSVRecord csvRecord : csvRecords) {
                createWarehouseFromCSV(csvRecord,
                        createLocationFromCSV(csvRecord),
                        createArticleFromCSV(csvRecord));
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
        log.info("successfully saved csv content to database");
    }

    /**
     * persists all warehouses from a csv into the database
     * @param csvRecord the current row of the csv file
     * @param location the location that was already persisted belonging to this warehouse-entry
     * @param article the article that was already persisted belonging to this warehouse-entre
     */
    private void createWarehouseFromCSV(CSVRecord csvRecord, Location location, Article article) {
        Warehouse warehouse = new Warehouse(
                article, location,
                Integer.parseInt(csvRecord.get("Quantity"))
        );

        // this extracts new objects for warehouse
        // and saves it into the DB. If the warehouse already exists, the existing warehouse gets updated.
        if (warehouseRepository.findWarehouseByArticleAndLocation(article, location).isEmpty()) {
            warehouseRepository.save(warehouse);
        } else {
            Warehouse updatedWarehouse = warehouseRepository.findWarehouseByArticleAndLocation(article, location).get();
            updatedWarehouse.setLocation(warehouse.getLocation());
            updatedWarehouse.setQuantity(warehouse.getQuantity());
            updatedWarehouse.setArticle(warehouse.getArticle());
            updatedWarehouse.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            warehouseRepository.save(updatedWarehouse);
        }
    }

    /**
     * persists an article from a csv into the database
     * @param csvRecord the current row of the csv file
     */
    private Article createArticleFromCSV(CSVRecord csvRecord) {

        Article article = new Article(
                csvRecord.get("Article"),
                createCategoryFromCSV(csvRecord),
                createCurrencyFromCSV(csvRecord),
                createStatusFromCSV(csvRecord),
                Double.parseDouble(csvRecord.get("Amount")));

        if (articleRepository.findArticleByDescription(article.getDescription()).isEmpty()) {
            articleRepository.save(article);
        } else {
            Article updatedArticle = articleRepository.findArticleByDescription(article.getDescription()).get();

            updatedArticle.setDescription(article.getDescription());
            updatedArticle.setCategory(article.getCategory());
            updatedArticle.setStatus(article.getStatus());
            updatedArticle.setCurrency(article.getCurrency());
            updatedArticle.setAmount(article.getAmount());
            updatedArticle.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            articleRepository.save(updatedArticle);
            article = updatedArticle;
        }
        return article;
    }

    /**
     * persists a status from a csv into the database
     * @param csvRecord the current row of the csv file
     */
    private Status createStatusFromCSV(CSVRecord csvRecord) {
        Status status = new Status(csvRecord.get("Status"));

        Optional<Status> optionalStatus = statusRepository.findStatusByDescription(status.getDescription());

        return saveToRepo(statusRepository, optionalStatus, status);
    }

    /**
     * persists a location from a csv into the database
     * @param csvRecord the current row of the csv file
     */
    private Location createLocationFromCSV(CSVRecord csvRecord) {
        Location location = new Location(
                csvRecord.get("Aisle"),
                Integer.parseInt(csvRecord.get("Shelf")),
                Integer.parseInt(csvRecord.get("Tray"))
        );
        Optional<Location> optionalLocation = locationRepository.findLocationByAisleAndShelfAndTray(location.getAisle(), location.getShelf(), location.getTray());

        return saveToRepo(locationRepository, optionalLocation, location);
    }

    /**
     * persists a category from a csv into the database
     * @param csvRecord the current row of the csv file
     */
    private Category createCategoryFromCSV(CSVRecord csvRecord) {
        Category cat = new Category(
                csvRecord.get("Category")
        );

        Optional<Category> optionalCategory = categoryRepository.findCategoryByDescription(cat.getDescription());

        return saveToRepo(categoryRepository, optionalCategory, cat);
    }

    /**
     * persists a currency from a csv into the database
     * @param csvRecord the current row of the csv file
     */
    private Currency createCurrencyFromCSV(CSVRecord csvRecord) {
        Currency currency = new Currency(
                csvRecord.get("CurrencyCode"),
                csvRecord.get("Country")
        );
        Optional<Currency> optionalCurrency = currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(), currency.getCountry());

        return saveToRepo(currencyRepository, optionalCurrency, currency);
    }

    /**
     * checks if entity already exists in the database and either save it as new or updates the existing one
     * @param repo the repository to use for database interaction
     * @param entryFromDatabase the same entry from the database that already exists or not
     * @param newEntry the entry from the csv to persist or update the entryFromDatabase
     * @return the entity that is saved or updated to the database
     */
    private <Repo extends JpaRepository, Entity extends BaseEntity> Entity saveToRepo(Repo repo, Optional<Entity> entryFromDatabase, Entity newEntry) {
        if (entryFromDatabase.isEmpty()) {
            return (Entity) repo.save(newEntry);
        }
        return entryFromDatabase.get();
    }
}