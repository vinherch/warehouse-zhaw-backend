package com.example.warehousesystem.utils;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author dejan.kosic
 */
@NoArgsConstructor
public class CsvCreator implements HasLogger{
    Logger logger = getLogger();
    private static final String TYPE = "text/csv";

    /**
     * Creates a file with content
     * @param valueMap values for putting into the file
     * @param fileDestinationPath destination where the file wants to be stored
     * @param fileName the name of the file
     * @param fileSeparator the file separator which should be used to separate the values
     * @return the path inclusive filename where the file is saved
     * @throws IOException - file related exceptions
     */
    public String createCsv(Map<String,String> valueMap, String fileDestinationPath, String fileName, String fileSeparator) throws IOException {
        StringBuilder text= new StringBuilder();
        for(String key : valueMap.keySet()) {
            text.append(key).append(fileSeparator).append(valueMap.get(key)).append("\n");
        }

            FileWriter fileWriter = new FileWriter(fileDestinationPath+"\\"+fileName);
            fileWriter.write(text.toString());
            fileWriter.close();


        logger.info("File has been created");
        return fileDestinationPath+"\\"+fileName;
    }

    /**
     * checks if the uploaded file has a csv format
     * @param file the uploaded file
     */
    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

}
