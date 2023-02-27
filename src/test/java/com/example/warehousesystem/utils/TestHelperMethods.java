package com.example.warehousesystem.utils;

import com.example.warehousesystem.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestHelperMethods {

    /**
     * Helper function to convert an Entity to JsonString for testing
     * @param obj Object to be converted to json
     */
    public static String asJson(final Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to create a dummy Category for testing
     * @return a dummy category object
     */
    public static Category createDummyCategory() {
        Category category = new Category();
        category.setId(1);
        category.setDescription("DummyCategory");
        return category;
    }

    /**
     * Helper function to create a dummy Currency for testing
     * @return a dummy currency object
     */
    public static Currency createDummyCurrency() {
        Currency currency = new Currency();
        currency.setId(1);
        currency.setCountry("Schweiz");
        currency.setCurrencyCode("CHF");
        return currency;
    }

    /**
     * Helper function to create a dummy Status for testing
     * @return a dummy Status object
     */
    public static Status createDummyStatus() {
        Status status = new Status();
        status.setId(1);
        status.setDescription("DummyStatus");
        return status;
    }

    /**
     * Helper function to create a dummy Article for testing
     * @return a dummy Article object
     */
    public static Article createDummyArticle() {
        Article article = new Article();
        article.setId(1);
        article.setDescription("DummyArticle");
        return article;
    }

    /**
     * Helper function to create a dummy Location for testing
     * @return a dummy location object
     */
    public static Location createDummyLocation() {
        Location location = new Location();
        location.setId(1);
        location.setAisle("DummyAisle");
        location.setShelf(88);
        location.setTray(99);
        return location;
    }

    /**
     * Helper function to create a dummy Status for testing
     * @return a dummy Status object
     */
    public static Warehouse createDummyWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1);
        warehouse.setQuantity(100);
        return warehouse;
    }
}
