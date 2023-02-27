package com.example.warehousesystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dejan.kosic
 * This is the entity class for the article entity
 *
 * Articles with their description, category, currency, status
 * and amount are stored in this entity
 */

@NoArgsConstructor
@Data
@ToString
@Table(name = "Article")
@Entity(name = "Article")
public class Article extends BaseEntity{
    @Column(
            name="Description",
            unique = true,
            nullable = false
    )
    private String description;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "category_id",nullable = false,foreignKey = @ForeignKey(name = "categoryId"))
    private Category category;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "currency_id",nullable = false,foreignKey = @ForeignKey(name = "currencyId"))
    private Currency currency;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "status_id",nullable = false,foreignKey = @ForeignKey(name = "statusId"))
    private Status status;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = Warehouse.class,orphanRemoval = true)
    @JoinColumn(name = "Article_ID",referencedColumnName = "id")
    private List<Warehouse> warehouses = new ArrayList<>();

    @Column(
            name="Amount",
            nullable = false
    )
    private double amount;

    /**
     * Custom constructor for the article entity
     * @param description description for the article
     * @param category category for the article
     * @param currency currency for the article
     * @param status status for the article
     * @param amount value of the article
     */
    public Article(String description, Category category, Currency currency, Status status, double amount) {
        super();
        this.description = description;
        this.category = category;
        this.currency = currency;
        this.status = status;
        this.amount = amount;
    }
}
