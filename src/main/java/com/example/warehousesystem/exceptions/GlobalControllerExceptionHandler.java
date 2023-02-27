package com.example.warehousesystem.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author yasmin.rosskopf
 * Class for global exception handling
 */
@ControllerAdvice
class GlobalControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    /**
     * Handles SQL-Exceptions
     * @param ex the exception which was thrown
     * @return errormessage
     */
    @ExceptionHandler(SQLException.class)
    public String handleSQLException(Exception ex){
        logger.error("SQLException Occured:: error: " + ex.getMessage());
        return "database_error";
    }

    /**
     * Handles IO-Exceptions
     * @param e the exception which was thrown
     * @return ResponseEntity with the HTTPStatusCode of NOT_FOUND (404)
     */
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="IOException occured")
    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleIOException(IOException e){
        logger.error("IOException handler executed");
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

    /**
     * Handles Data Integrity Violation Exceptions
     * @param e the exception which was thrown
     * @return @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="DataIntegrityViolationException occured")
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        logger.error("DataIntegrityViolationException handler executed");
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Index out of bound exceptions
     * @param e the exception which was thrown
     * @return @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ExceptionHandler({IndexOutOfBoundsException.class})
    public ResponseEntity<Object> handleOutOfBoundException(IndexOutOfBoundsException e){
        logger.error("IndexOutOfBoundException handler executed");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Resource not found exceptions
     * @param e the exception which was thrown
     * @return @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e){
        logger.error("Resource Id not found handler executed");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles a record already exists in the database
     * @param e the exception which was thrown
     * @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<Object> handleRecordAlreadyExistException(RecordAlreadyExistsException e){
        logger.error("RecordAlreadyExists handler executed." );
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles when a number is entered which is equals or lower than zero
     * @param e the exception which was thrown
     * @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ExceptionHandler(InvalidFormatEntryException.class)
    public ResponseEntity<Object> handleInvalidFormatEntryException(InvalidFormatEntryException e){
        logger.error("InvalidFormatEntry handler executed." );
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    /**
     * This exception is thrown when no articles were found for the semiautomatic order
     * @param e the exception which was thrown
     * @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400) and the exception message
     */
    @ExceptionHandler(NoArticlesFoundForOrderException.class)
    public ResponseEntity<Object> handleNoArticlesFoundForOrderException(NoArticlesFoundForOrderException e){
        logger.error("NoArticlesFoundForOrderException handler executed!");
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    /**
     * This is the default method which is thrown when all exceptionHandler above do not match the exception.
     * This method has to be THE LAST ONE in the class.
     * @return ResponseEntity with the HHTPStatusCode of BAD REQUEST (400)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception e) {
        logger.error("A generic exception was thrown: "  + e.getMessage());
        return new ResponseEntity<>("Internal server error", HttpStatus.BAD_REQUEST);
    }

}