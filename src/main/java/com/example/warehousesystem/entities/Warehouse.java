package com.example.warehousesystem.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author dejan.kosic
 * This is the entity class for the warehouse entity
 *
 * Warehouse with their article entries, the location of it and the quantity
 */
@NoArgsConstructor
@Data
@ToString
@Table(name = "Warehouse",uniqueConstraints = @UniqueConstraint(name= "U_A_L",columnNames = {"article_id","location_id"}))
@Entity(name = "Warehouse")
public class Warehouse extends BaseEntity{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="article_id",nullable = false,foreignKey = @ForeignKey(name = "articleId"))
    private Article article;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="location_id",nullable = false,foreignKey = @ForeignKey(name = "locationId"))
    private Location location;

    @Column(
            name="Quantity",
            nullable = false
    )
    private int quantity;

    /**
     * Custom constructor for the warehouse entity
     * @param article article for the warehouse position
     * @param location location for the warehouse position
     * @param quantity quantity of the article at the given location
     */
    public Warehouse(Article article, Location location, int quantity) {
        super();
        this.article = article;
        this.location = location;
        this.quantity = quantity;
    }
}
