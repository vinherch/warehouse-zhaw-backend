package com.example.warehousesystem.exceptions;

/**
 * @author dejan.kosic
 * Exception class when no articles were found for order
 */
public class NoArticlesFoundForOrderException extends Exception{
    public NoArticlesFoundForOrderException(String message){
        super(message);
    }
}
