package com.example.warehousesystem.exceptions;

/**
 * Exception class if entries are not valid
 */
public class InvalidFormatEntryException extends Exception{
    public InvalidFormatEntryException(String message){
        super(message);
    }
}
