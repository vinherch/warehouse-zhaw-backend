package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Status;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.StatusRepository;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author dejan.kosic
 * Service class for the status controller
 */
@Service
@AllArgsConstructor
public class StatusService implements HasLogger {
    private final Logger logger = getLogger();
    private final StatusRepository statusRepository;
    private CsvExportService csvExportService;

    /**
     * Gets a list of all statuses
     * @return list of all statuses
     */
    public List<Status> getAllStatus(){
        return statusRepository.findAll();
    }

    /**
     * Gets a specific status by id
     * @param id id of the status
     * @return status with provided id
     */
    public Status getStatusById(long id){
        return statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status does not exist with id: " + id));
    }

    /**
     * Adds a new status to database
     * @param status status to be added
     * @return added status
     * @throws RecordAlreadyExistsException if status already exists in database
     */
    public Status addStatus(Status status) throws RecordAlreadyExistsException {
        Optional<Status> existingStatus = statusRepository.findStatusByDescription(status.getDescription());
        if(existingStatus.isPresent()){
            throw new RecordAlreadyExistsException("Status already exists in database!");
        }
        statusRepository.save(status);
        return statusRepository.findStatusByDescription(status.getDescription()).get();
    }

    /**
     * Modifies a status
     * @param status modified status object
     * @param id id of the status to be modified
     */
    public void modifyStatusById(Status status,Long id){
        Status updatedStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status does not exist with id: " + id));
        logger.info("status with id "+ id+" found and updating...");
        updatedStatus.setDescription(status.getDescription());
        updatedStatus.setModifiedTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statusRepository.save(updatedStatus);
    }

    /**
     * deletes status with provided id
     * @param id id of the status to be deleted
     */
    public void deleteStatusById(long id){
        statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status does not exist with id: " + id));
        logger.info("status with id "+ id+" found and deleting...");
        statusRepository.deleteById(id);
    }

    /**
     * Saves status to csv and adds file to HTTP servlet response
     * @param servletResponse HTTP servlet response tho attach the csv file to
     * @throws IOException - file related exceptions
     */
    public void getCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"status.csv\"");
        csvExportService.writeStatusToCsv(servletResponse.getWriter());
    }

}
