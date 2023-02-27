package com.example.warehousesystem.utils;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dejan.kosic
 * Test for utility class "CsvCreator.java"
 */
public class CsvCreatorTest {
    private CsvCreator cs;
    private Map<String,String> testValues;

    private String fileDestination;
    private String fileName;

    private String fileSeparator;

    private File file;

    /**
     * Sets everything up for the test
     */
    @BeforeEach
    void setUp() {
        testValues = new HashMap<>();
        testValues.put("2","Besen");
        testValues.put("1","Schuhe");

        fileDestination = "src/test/java/com/example/warehousesystem/utils";

        fileName = "TestFile.csv";

        fileSeparator=",";
        file = new File(fileDestination+"\\"+fileName);
        cs = new CsvCreator();

    }

    /**
     * teardown after each test
     */
    @AfterEach
    void teardown(){
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * tests if the file is created successfully and if the content is right
     * @result csv creation content and creation is right.
     */
    @Test
    public void testCreateCsv() throws IOException {
    String text = "";
    int content = 0;
    assertTrue(cs.createCsv(testValues,fileDestination,fileName,fileSeparator).length()>0,"File path is empty!");
    assertEquals(fileDestination+"\\"+fileName,cs.createCsv(testValues,fileDestination,fileName,fileSeparator),"File path not correct!");
    FileReader fileReader = new FileReader(file);
    while((content = fileReader.read())!=-1){
            text+=(char) content;
        }
    fileReader.close();
    assertEquals("1",text.substring(0,1), "Content is not right!");
    assertEquals(3,text.split(fileSeparator).length,"File seperator does not separate correctly!");

    }


}


