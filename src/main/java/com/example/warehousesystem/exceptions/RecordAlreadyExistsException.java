package com.example.warehousesystem.exceptions;

/**
 * Exception class when record already exists in database
 */
public class RecordAlreadyExistsException extends Exception{

     public RecordAlreadyExistsException(String message){
         super(message);
     }
}
