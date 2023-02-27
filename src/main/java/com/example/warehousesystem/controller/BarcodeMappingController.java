package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.*;
import com.example.warehousesystem.exceptions.InvalidFormatEntryException;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.*;
import com.example.warehousesystem.utils.BarcodeHelper;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
/**
 * @author dejan.kosic
 * Controller class that provides CRUD-operation endpoints in regard to the barcode mapping entity
 * the RestController annotation ensures that the object returned is automatically serialized into JSON and
 * passed back into the HttpResponse object
 */
@RestController
@RequestMapping(path="/v1/barcodemappings")
public class BarcodeMappingController implements HasLogger {

    private final Logger logger = getLogger();

    private final BarcodeMappingService barcodeMappingService;

    private final ArticleService articleService;
    private final CurrencyService currencyService;
    private final StatusService statusService;
    private final CategoryService categoryService;

    /**
     * Custom constructor for the Barcode Mapping Controller
     *
     * @param barcodeMappingService barcode mapping service of the barcode mapping entity
     * @param articleService article service ot the article entity
     * @param currencyService currency service of the currency entity
     * @param statusService status service of the status entity
     * @param categoryService category service of the category entity
     */
    public BarcodeMappingController(BarcodeMappingService barcodeMappingService, ArticleService articleService,CurrencyService currencyService,StatusService statusService,CategoryService categoryService) {
        this.barcodeMappingService=barcodeMappingService;
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
        this.statusService = statusService;

    }

    /**
     * GET endpoint to fetch all barcode mappings in the database
     * @return a list of all barcode mappings
     */
    @GetMapping
    public List<BarcodeMapping> getAllBarcodeMappingEntries(){
        logger.info("get all barcode mappings");
        return barcodeMappingService.getAllBarcodeMappings();
    }

    /**
     * GET endpoint to fetch a specific barcode mapping by ID in the database
     * @param id the id of the barcode mapping that is to be found
     * @return the specific barcode mapping
     */
    @GetMapping("/{id}")
    public BarcodeMapping getBarcodeMappingEntryById(@PathVariable long id){
        logger.info("get barcode mapping with id "  + id);
        return barcodeMappingService.getBarcodeMappingById(id);
    }

    /**
     * GET endpoint to fetch a csv-file with all barcode mappings in the database
     * @param servletResponse encloses the csv-file in the response
     */
    @GetMapping("/csv")
    public void getBarcodeMappingEntriesAsCsv(HttpServletResponse servletResponse) throws IOException {
        logger.info("get csv with barcode mappings");
        this.barcodeMappingService.getCsv(servletResponse);
    }

    /**
     * POST endpoint to create a new article according to ean-description mapping in the barcode mapping entity
     * @param barcodeHelper the barcode object which was scanned
     * @throws RecordAlreadyExistsException if article already exists in database or
     * if the article amount is lower or equals zero
     */
   @PostMapping("/scan")
   @ResponseStatus(HttpStatus.CREATED)
    public void scanNewArticle(@RequestBody BarcodeHelper barcodeHelper) throws RecordAlreadyExistsException, InvalidFormatEntryException {
       BarcodeMapping foundBarcodeMapping = barcodeMappingService.getBarcodeMappingByEan(barcodeHelper.getBarcodeNumber());
       Category category = categoryService.getCategoryById(1);
       Currency currency = currencyService.getCurrencyById(1);
       Status status = statusService.getStatusById(1);
       Article insertedArticle = articleService.addArticle(new Article(foundBarcodeMapping.getDescription(),category,currency,status,1.00));
       logger.info("Article with id "+insertedArticle.getId() +" successfully created!");
   }

    /**
     * POST endpoint to create a new barcode mapping in the database
     * @param barcodeMapping the barcode mapping object to be created
     * @return the created barcode mapping
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BarcodeMapping insertNewBarcodeMappingEntry(@RequestBody BarcodeMapping barcodeMapping) throws RecordAlreadyExistsException {
        logger.info("creating barcode mapping...");
        BarcodeMapping insertedBarcodeMapping = barcodeMappingService.addNewBarcodeMapping(barcodeMapping);
        logger.info("barcode mapping with id: " + insertedBarcodeMapping.getId()+" successfully created!");
        return insertedBarcodeMapping;
    }

    /**
     * PUT endpoint to update a specific barcode mapping by ID in the database
     * @param barcodeMapping modified barcode mapping object
     * @param id the id of the barcode mappings that is to be updated
     */
    @PutMapping("/{id}")
    public void modifyBarcodeMappingById(@RequestBody BarcodeMapping barcodeMapping, @PathVariable long id){
        logger.info("updating category with id: " + id+"...");
        barcodeMappingService.modifyBarcodeMappingById(barcodeMapping,id);
        logger.info("category with id: " + id + " updated!");
    }

    /**
     * DELETE endpoint to delete an existing barcode mapping in the database
     * @param id the id of the specific barcode mapping to be deleted
     */
    @DeleteMapping  ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBarcodeMappingById(@PathVariable("id") long id){
        logger.info("deleting barcode mapping with id "  + id+"...");
        barcodeMappingService.deleteBarcodeMappingById(id);
        logger.info("barcode mapping with id "+ id +" successfully deleted!");
    }
}
