package com.example.warehousesystem.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CSVEmptyCellException extends RuntimeException{

    public CSVEmptyCellException(String message){
        super(message);
    }
}
