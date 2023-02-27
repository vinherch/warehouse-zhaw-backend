package com.example.warehousesystem.service;

import com.example.warehousesystem.ManagementToolApplication;
import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.repository.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(profiles = {"dev", "h2"})
@SpringBootTest(classes = ManagementToolApplication.class)
class CSVImportServiceTest {

    @Autowired
    private CSVImportService csvImportService;
    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void savesNewEntitiesToDatabase_When_csvFileIsUploaded() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.csv",
                "text/csv",
                """
                Article,Category,Amount,CurrencyCode,Country,Status,Aisle,Shelf,Tray,Quantity
                Sauser,Alkoholische Getränke,4.8,CHF,Schweiz,ACTIVE,A,1,1,50
                """.getBytes()
        );

        csvImportService.saveAllEntitiesToDBFromCSV(file);

        Article article = articleRepository.findArticleByDescription("Sauser").get();
        assertEquals(4.8, article.getAmount());
        assertEquals("CHF", article.getCurrency().getCurrencyCode());
        assertEquals("Alkoholische Getränke", article.getCategory().getDescription());
    }

    @Test
    void updatesExistingEntitiesInDatabase_When_csvFileIsUploaded() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.csv",
                "text/csv",
                """
                Article,Category,Amount,CurrencyCode,Country,Status,Aisle,Shelf,Tray,Quantity
                Sauser,Alkoholische Getränke,4.8,CHF,Schweiz,ACTIVE,A,1,1,50
                Sauser,Getränke,6,EUR,Deutschland,ACTIVE,A,1,1,50
                """.getBytes()
        );

        csvImportService.saveAllEntitiesToDBFromCSV(file);

        Article article = articleRepository.findArticleByDescription("Sauser").get();
        assertEquals(6, article.getAmount());
        assertEquals("EUR", article.getCurrency().getCurrencyCode());
        assertEquals("Getränke", article.getCategory().getDescription());
    }

    @Test
    void throwsIllegalArgumentException_When_csvFileIsNotCorrectlySetUp() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MockMultipartFile file
                    = new MockMultipartFile(
                    "file",
                    "hello.csv",
                    "text/csv",
                    """
                    Article,Category,Amount,Country,Status,Aisle,Shelf,Tray,Quantity
                    Sauser,Alkoholische Getränke,CHF,Schweiz,ACTIVE,A,1,1,50
                    """.getBytes()
            );
            csvImportService.saveAllEntitiesToDBFromCSV(file);
        });
    }
}