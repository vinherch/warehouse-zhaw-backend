package com.example.warehousesystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author dejan.kosic
 * This is the entity class for the barcode mapping entity
 *
 * EAN-Barcodes and their corresponding article description are stored in this entity e.g. ean "54490002857806",
 * description "Fanta Himbeere"
 */
@NoArgsConstructor
@Data
@ToString
@Table(name = "BarcodeMapping",uniqueConstraints = @UniqueConstraint(name= "U_E_D",columnNames = {"ean","description"}))
@Entity(name = "BarcodeMapping")
public class BarcodeMapping extends BaseEntity{
    @Column(
            name="EAN",
            nullable = false
    )
    private String ean;
    @Column(
            name="Description",
            nullable = false
    )
    private String description;

/**
 * Custom constructor for the barcode mapping entity class
 * @param ean the ean for the article
 * @param description article description
 */
    public BarcodeMapping(String ean,String description){
        this.ean = ean;
        this.description=description;
    }

}

