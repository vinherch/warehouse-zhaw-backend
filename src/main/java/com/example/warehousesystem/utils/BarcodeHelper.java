package com.example.warehousesystem.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * @autor dejan.kosic
 * DTO class for the barcode so barcode mapping controller can handle incoming barcode scan requests
 */
public class BarcodeHelper {
    private String barcodeNumber;
}
