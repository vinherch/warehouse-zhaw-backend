package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dejan.kosic
 * This is the repository for the entity "Location"
 */
@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
    /**
     * Finds the location by aisle,shelf and tray
     * @param aisle aisle of the location
     * @param shelf shelf of the location
     * @param tray tray of the location
     * @return Option with the location if it exists
     */
    Optional<Location> findLocationByAisleAndShelfAndTray(String aisle, int shelf, int tray);
}
