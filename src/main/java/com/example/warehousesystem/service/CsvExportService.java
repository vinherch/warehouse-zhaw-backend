package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.*;
import com.example.warehousesystem.repository.*;
import com.example.warehousesystem.utils.HasLogger;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author dejan.kosic
 * CSVExport Service class which provides csv writing capability for all entities
 */
@AllArgsConstructor
@Service
public class CsvExportService implements HasLogger {
    private final Logger log = getLogger();

    private final ArticleRepository articleRepository;
    private final CurrencyRepository currencyRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final StatusRepository statusRepository;
    private final WarehouseRepository warehouseRepository;
    private final BarcodeMappingRepository barcodeMappingRepository;


    /**
     * writes all articles in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeArticlesToCsv(Writer writer) {

        List<Article> articles = articleRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Article Id", "Description", "Category Description", "Amount", "Currency", "Status");
            for (Article Article : articles) {
                csvPrinter.printRecord(
                        Article.getId(),
                        Article.getDescription(),
                        Article.getCategory().getDescription(),
                        Article.getAmount(),
                        Article.getCurrency().getCurrencyCode(),
                        Article.getStatus().getDescription()
                );
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all currencies in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeCurrenciesToCsv(Writer writer) {

        List<Currency> currencies = currencyRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Currency Id", "Code", "Country");
            for (Currency currency : currencies) {
                csvPrinter.printRecord(currency.getId(), currency.getCurrencyCode(), currency.getCountry());
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all locations in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeLocationsToCsv(Writer writer) {

        List<Location> locations = locationRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Location Id", "Aisle", "Shelf", "Tray");
            for (Location location : locations) {
                csvPrinter.printRecord(location.getId(), location.getAisle(), location.getShelf(), location.getTray());
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all barcodeMappings in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeBarcodeMappingToCsv(Writer writer) {
        List<BarcodeMapping> barocdeMappingList = barcodeMappingRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Barcode Id", "EAN", "Description");
            for (BarcodeMapping barcodeMapping : barocdeMappingList) {
                csvPrinter.printRecord(barcodeMapping.getId(), barcodeMapping.getEan(),barcodeMapping.getDescription());
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all categories in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeCategoriesToCsv(Writer writer) {

        List<Category> categories = categoryRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Category Id", "Description");
            for (Category category : categories) {
                csvPrinter.printRecord(category.getId(), category.getDescription());
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all status in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeStatusToCsv(Writer writer) {

        List<Status> statusList = statusRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Status Id", "Description");
            for (Status status : statusList) {
                csvPrinter.printRecord(status.getId(), status.getDescription());
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }

    /**
     * writes all warehouses in the database to a csv
     * @param writer the printwriter of the HTTP servlet response
     */
    public void writeWarehouseToCsv(Writer writer) {

        List<Warehouse> warehouses = warehouseRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("Warehouse Id", "Quantity", "Article", "Category", "Amount", "CurrencyCode", "Country", "Status", "Aisle", "Shelf","Tray");
            for (Warehouse warehouse : warehouses) {
                csvPrinter.printRecord(
                        warehouse.getId(),
                        warehouse.getQuantity(),
                        warehouse.getArticle().getDescription(),
                        warehouse.getArticle().getCategory().getDescription(),
                        warehouse.getArticle().getAmount(),
                        warehouse.getArticle().getCurrency().getCurrencyCode(),
                        warehouse.getArticle().getCurrency().getCountry(),
                        warehouse.getArticle().getStatus().getDescription(),
                        warehouse.getLocation().getAisle(),
                        warehouse.getLocation().getShelf(),
                        warehouse.getLocation().getTray()
                );
            }
        } catch (IOException e) {
            log.error("Error writing CSV ", e);
        }
    }
}
