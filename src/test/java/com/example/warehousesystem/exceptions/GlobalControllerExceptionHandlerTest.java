package com.example.warehousesystem.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yasmin.rosskopf
 * Test class for the Global Exception Handler
 */
class GlobalControllerExceptionHandlerTest {

    private GlobalControllerExceptionHandler handler;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        handler = new GlobalControllerExceptionHandler();
    }

    /**
     * Test if a sql-exception returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_SQLException() {
        assertEquals(
                handler.handleSQLException(
        new SQLException("testmessage")), "database_error");
    }
    /**
     * Test if an IndexOutOfBoundException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_IndexOutOfBoundException() {
        assertEquals(
                handler.handleOutOfBoundException(
                        new IndexOutOfBoundsException("testmessage")),
                new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
    /**
     * Test if an IOException returns a not found response 404
     * @result 404 NOT FOUND
     */
 @Test
    public void returnsBadRequest_When_IOException() {
        assertEquals(
                new ResponseEntity<>("testmessage", HttpStatus.NOT_FOUND),
                handler.handleIOException(
                        new IOException("testmessage")));
    }
    /**
     * Test if an IndexOutOfBoundException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_ResourceNotFoundException() {
        assertEquals(
                new ResponseEntity<>(HttpStatus.BAD_REQUEST),
                handler.handleResourceNotFoundException(
                        new ResourceNotFoundException("testmessage")));
    }
    /**
     * Test if an RecordAlreadyExistException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_RecordAlreadyExistsException() {
        assertEquals(
                new ResponseEntity<>("testmessage", HttpStatus.BAD_REQUEST ),
                handler.handleRecordAlreadyExistException(
                        new RecordAlreadyExistsException("testmessage")));
    }

    /**
     * Test if an InvalidFormatEntryException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_InvalidFormatEntryException() {
        assertEquals(
                new ResponseEntity<>("testmessage", HttpStatus.BAD_REQUEST ),
                handler.handleInvalidFormatEntryException(
                        new InvalidFormatEntryException("testmessage")));
    }
    /**
     * Test if an DataIntegrityViolationException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_DataIntegrityViolationException() {
        assertEquals(
                new ResponseEntity<>("testmessage", HttpStatus.BAD_REQUEST),
                handler.handleDataIntegrityViolationException(
                        new DataIntegrityViolationException("testmessage")));
    }

    /**
     * Test if an RecordAlreadyExistException returns a bad request 400
     * @result 400 BAD REQUEST
     */
    @Test
    public void returnsBadRequest_When_AnotherException() {
        assertEquals(
                handler.handleGenericException(
                        new NullPointerException("testmessage")),
                new ResponseEntity<>( "Internal server error",HttpStatus.BAD_REQUEST ));
    }
}