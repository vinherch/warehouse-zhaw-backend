package com.example.warehousesystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author dejan.kosic
 * This is the entity class for the location entity
 *
 * Locations with their aisle,shelf and tray alre stored in this entity
 */

@NoArgsConstructor
@Data
@ToString
@Table(name="Location",uniqueConstraints = @UniqueConstraint(name= "U_A_S_T",columnNames = {"aisle","shelf","tray"}))
@Entity(name="Location")
public class Location extends BaseEntity{
    @Column(
            name="Aisle",
            nullable = false
    )
    private String aisle;
    @Column(
            name="Shelf",
            nullable = false
    )
    private int shelf;
    @Column(
            name="Tray",
            nullable = false
    )
    private int tray;

    @JsonIgnore
    @OneToMany(mappedBy = "location",cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = Warehouse.class,orphanRemoval = true)
    private List<Warehouse> warehouses;

    /**
     * Custom constructor for the location entity
     * @param aisle aisle of the location
     * @param shelf shelf of the location
     * @param tray tray of the location
     */
    public Location(String aisle, int shelf, int tray) {
        super();
        this.aisle = aisle;
        this.shelf = shelf;
        this.tray = tray;

    }
}
