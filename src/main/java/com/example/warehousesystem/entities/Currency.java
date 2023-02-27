package com.example.warehousesystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author dejan.kosic
 * This is the entity class for the currency entity
 *
 * Currencies and their currency code inklusive currency country are stored in this entity e.g. currency code "USD",
 * country "U.S.A."
 */
@Data
@NoArgsConstructor
@ToString
@Table(name="Currency",uniqueConstraints = @UniqueConstraint(name= "U_CC_C",columnNames = {"currencyCode","country"}))
@Entity(name="Currency")
public class Currency extends BaseEntity{

    @Column(
            name="CurrencyCode",
            nullable = false
    )
   private String currencyCode;
    @Column(
            name="Country",
            nullable = false
    )
    private String country;

    @JsonIgnore
    @OneToMany(mappedBy = "currency",cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = Article.class,orphanRemoval = true)
    private List<Article> articles;

    /**
     * Custom constructor for the currency entity
     * @param currencyCode currency code for the currency
     * @param country  country name for the currency
     */
    public Currency(String currencyCode, String country) {
        super();
        this.currencyCode = currencyCode;
        this.country = country;
    }

}
