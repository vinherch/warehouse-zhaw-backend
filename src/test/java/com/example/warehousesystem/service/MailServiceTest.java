package com.example.warehousesystem.service;

import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.NoArticlesFoundForOrderException;
import com.example.warehousesystem.repository.ArticleRepository;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author dejan.kosic
 * Test for the mail service class
 */
@SpringBootTest(classes = MailService.class)
public class MailServiceTest {

    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private MimeMessage mimeMessage;

    @Autowired
    private MailService mailService;

    Map<String,String> testValues;
    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        testValues = new HashMap<>();
        testValues.put("1","Besen");
        testValues.put("2","Adidas Schuhe");
        mailService = new MailService(mailSender,articleRepository);
    }

    /**
     * Tests if the method throws NoArticleFoundForOrderException and if the exception message is correct
     * @result NoArticlesFoundForOrderException because there are no files for order
     */
    @Test
    void throwsNoArticlesFoundForOrderException_When_PrepareCsvFileToAttach() {
        NoArticlesFoundForOrderException naf = assertThrows(NoArticlesFoundForOrderException.class, () -> mailService.prepareArticlesForCsvOrder(0));
        assertEquals("No Articles found to order!", naf.getMessage(), "Exception Message wrong!");
    }

    /**
     * Tests if the Method ist getting the correct article list for the order
     * @result article list of all articles for the order
     */
    @Test
    void returnsArticlesFromGetAllArticlesWithLowQuantity_When_preparingArticlesForCsvOrder() throws NoArticlesFoundForOrderException{
        //given
        Article article = new Article();
        article.setId(1L);
        article.setDescription("Nike");
        List<Article> articles = new ArrayList<>();
        articles.add(article);
        //when
        when(articleRepository.getAllArticlesWithLowQuantity(500)).thenReturn(articles);
        //then
        assertEquals("Nike",mailService.prepareArticlesForCsvOrder(500).get("1"));
    }

    /**
     * Tests if the method has the right content and if the path and filename are correct
     * @result  path name and file name are correct
     */
    @Test
    void savesCsvFileLocally_When_CreateCsvOrderForMail() throws IOException {
        int content = 0;
        String text = "";
        ReflectionTestUtils.setField(mailService,"csvSavePath","src"+File.separator+"test"+File.separator+"java");
        File file = new File(mailService.createCsvOrderForMail(testValues));
        FileReader fileReader = new FileReader(file);
        while((content = fileReader.read())!=-1){
            text+=(char) content;
        }

        fileReader.close();
        assertEquals("Article_Order.csv", FilenameUtils.getName(file.getName()),"Wrong Filename!");
        assertEquals("1",text.substring(0,1));
        file.delete();
    }

    /**
     * Test if the mail was sent
     * @result if mail was sent
     *
     */
    @Test
    public void verifyCorrectMessage_When_SendingAMail() {
        //given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        String fileToAttach = "example.txt";
        ReflectionTestUtils.setField(mailService, "mailAddress", "from@test.com");

        //when
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendMailWithAttachment(to, subject, body, fileToAttach);

        //then
        verify(mailSender).send(mimeMessage);
        System.out.println(mimeMessage);

    }
}
