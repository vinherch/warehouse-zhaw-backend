package com.example.warehousesystem.repository;

import static org.junit.jupiter.api.Assertions.*;


import com.example.warehousesystem.entities.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author yasmin.rosskopf
 * Test for Repository class of the currency entity
 * only testing specifically implemented methods
 */
@DataJpaTest
public class CurrencyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Currency currency;
    /**
     * Set up before each test
     */
    @BeforeEach
    public void setup() {
        // given
        currency = new Currency("USD", "USA");
        entityManager.persist(currency);
        entityManager.flush();
    }

    /**
     * Test to find a Currency by CountryCode and Country
     * * @result verifies if the currency matches
     */
    @Test
    public void returnsCurrency_When_findCurrencyByCodeAndCountry() {
        // when
        Currency found = currencyRepository.findCurrencyByCurrencyCodeAndCountry(currency.getCurrencyCode(), currency.getCountry()).get();

        // then
        assertEquals(currency.getCurrencyCode(), found.getCurrencyCode());
        assertEquals(currency.getCountry(), found.getCountry());
    }
    /**
     * Test to find a Currency by Currency code and Country, whereby the Currency Code does not exist
     * @result verifies if the currency is empty
     */
    @Test
    public void returnsEmptyOptional_When_FindCurrencyByCodeAndCountryWithNonExistingCurrency() {
        // then
        assertTrue(currencyRepository.findCurrencyByCurrencyCodeAndCountry("CHF","USA").isEmpty());
    }

    /**
     * Test to find a Currency by Currency code and Country, whereby the Country does not exist
     * @result verifies if the currency is empty
     */
    @Test
    public void returnsEmptyOptional_When_FindCurrencyByCodeAndCountryWithNonExistingCountry() {
        // then
        assertTrue(currencyRepository.findCurrencyByCurrencyCodeAndCountry("USD","Schweiz").isEmpty());
    }

}