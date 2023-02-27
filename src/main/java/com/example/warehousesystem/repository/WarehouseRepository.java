package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.entities.Location;
import com.example.warehousesystem.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse,Long> {
    /**
     * Finds warehouse entry by article and location
     * @param article article of the warehouse entry
     * @param location location of the warehouse entry
     * @return Option with the warhouse if it exists
     */
    Optional<Warehouse> findWarehouseByArticleAndLocation(Article article, Location location);

    /**
     * Custom query to deleting warehouse entry by id
     * @param id id of the warehouse entry
     */
    @Modifying
    @Query("DELETE FROM Warehouse WHERE id=:id")
    void deleteWarehousebyId(long id);
}
