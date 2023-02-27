package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Status;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.exceptions.ResourceNotFoundException;
import com.example.warehousesystem.repository.StatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


/**
 * @author yasmin.rosskopf
 * Test for service class of entity "Status"
 */
@SpringBootTest(classes = StatusService.class)
public class StatusServiceTest {
    @MockBean
    private StatusRepository statusRepository;

    @MockBean
    private CsvExportService csvExportService;

    StatusService statusService;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        statusService = new StatusService(statusRepository, csvExportService);
    }

    /**
     * Test for service to fetch all statuses
     *
     * @result returns a list of statuses
     */
    @Test
    public void getsAListOfAllStatuss_When_getAllStatussIsCalled() {
        Status status = getStatus();
        List<Status> statuses = new ArrayList<>();
        statuses.add(status);
        when(statusRepository.findAll()).thenReturn(statuses);
        List<Status> result = statusService.getAllStatus();
        assertEquals(result.size(), 1);
    }

    /**
     * Test for service to fetch all statuses
     *
     * @result returns an empty list of statuses
     */
    @Test
    public void returnsEmptyList_When_getAllStatusesIsCalled() {
        List<Status> statuses = new ArrayList<>();
        when(statusRepository.findAll()).thenReturn(statuses);
        List<Status> result = statusService.getAllStatus();
        assertEquals(result.size(), 0);
    }

    /**
     * Test for service to fetch status with specific id
     *
     * @result returns an optional status
     */
    @Test
    public void getsStatusWithSpecificId_When_getStatusByIdIsCalled() {
        Status status = getStatus();
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Status result = statusService.getStatusById(1);
        assertEquals(result.getId(), 1);
    }

    /**
     * Test for service to add an status that already exists
     *
     * @result throws an Exception
     */
    @Test
    public void throwsAnRecordAlreadyExistsException_When_addStatusByIdThatExistsIsCalled() {
        Status status = getStatus();
        Assertions.assertThrows(RecordAlreadyExistsException.class, () -> {
            when(statusRepository.findStatusByDescription(status.getDescription())).thenReturn(Optional.of(status));
            statusService.addStatus(status);
        });
    }

    /**
     * Test for service to add a new status
     *
     * @result returns the new status
     */
    @Test
    public void returnsTheNewStatus_When_addStatusByIdThatExistsIsCalled() throws RecordAlreadyExistsException {
        Status status = getStatus();
        when(statusRepository.findStatusByDescription(status.getDescription())).thenReturn(Optional.empty()).thenReturn(Optional.of(status));
        when(statusRepository.save(status)).thenReturn(status);
        Status newStatus = statusService.addStatus(status);
        assertEquals(status, newStatus);
    }

    /**
     * Test for service to delete an status by ID
     *
     * @result calls statusRepository to delete status
     */
    @Test
    public void callsStatusRepositoryToDeleteStatusWithSpecificId_When_deleteStatusByIdIsCalled() {
        Status status = getStatus();
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        doNothing().when(statusRepository).deleteById(1L);
        statusService.deleteStatusById(1L);
        verify(statusRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for service to delete a status that does not exist
     * @result throws an Exception
     */
    @Test
    public void throwsResourceNotFoundException_When_deleteStatusByIdThatDoesNotExistIsCalled() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            doNothing().when(statusRepository).deleteById(1L);
            statusService.deleteStatusById(1L);
        });
    }

    /**
     * Test for service to correctly process the HTTPServletResponse
     * @throws IOException if status service fails
     * @result calls the csvExportService for status
     */
    @Test
    public void callsCsvExportService_When_getCSVIsCalled() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        statusService.getCsv(response);
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=\"status.csv\"", response.getHeader("Content-Disposition"));
        verify(csvExportService, times(1)).writeStatusToCsv(response.getWriter());
    }

    /**
     * Test for service to modify a status with specific id
     * @result calls statusRepository to save updated status
     */
    @Test
    public void callsRepositoryToModifyStatusWithSpecificId_When_modifyStatusByIdIsCalled() {
        Status status = getStatus();
        Status modifiedStatus = new Status();
        modifiedStatus.setId(2L);
        modifiedStatus.setDescription("Inaktiv");

        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(statusRepository.save(modifiedStatus)).thenReturn(modifiedStatus);

        assertNotEquals(status, modifiedStatus);
        statusService.modifyStatusById(modifiedStatus, 1L);

        assertEquals(status, modifiedStatus);
        verify(statusRepository, times(1)).save(status);
    }

    /**
     * instantiates a dummy status for the tests
     *
     * @return a status
     */
    private Status getStatus() {
        Status status = new Status();
        status.setId(1L);
        status.setDescription("Aktiv");
        return status;
    }
}