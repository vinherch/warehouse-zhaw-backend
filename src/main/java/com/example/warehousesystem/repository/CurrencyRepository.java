package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dejan.kosic
 * This is the entity for the Table "Article"
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    /**
     * Find currency by currency code and country name
     * @param currencyCode three character currency code for the currency
     * @param country country name for the currency
     * @return Option with the currency if it exists
     */
    Optional<Currency> findCurrencyByCurrencyCodeAndCountry(String currencyCode, String country);
}
