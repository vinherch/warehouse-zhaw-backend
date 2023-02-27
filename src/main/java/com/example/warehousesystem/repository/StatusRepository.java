package com.example.warehousesystem.repository;

import com.example.warehousesystem.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dejan.kosic
 * This is the repository for the entity "Status"
 */
@Repository
public interface StatusRepository extends JpaRepository<Status,Long> {
    /**
     * Finds status entry by description
     * @param description description of the status
     * @return container Option with the status if it exists
     */
    Optional<Status> findStatusByDescription(String description);
}
