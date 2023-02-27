
package com.example.warehousesystem.service;


import com.example.warehousesystem.entities.Article;
import com.example.warehousesystem.exceptions.NoArticlesFoundForOrderException;
import com.example.warehousesystem.repository.ArticleRepository;
import com.example.warehousesystem.utils.CsvCreator;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dejan.kosic
 * Class for sending a Mail with attachment
 */

@Service
@Data
public class MailService implements HasLogger {
    Logger log = getLogger();
    @Value("${spring.mail.username}")
    private String mailAddress;
    @Value("${csv.save.path}")
    private String csvSavePath;
    @Value("${csv.file.separator}")
    private String fileCsvSeparator;

    private final JavaMailSender mailSender;

    private final ArticleRepository articleRepository;

    /**
     * Constructor for the mail service
     * @param mailSender mail sender object with relevant configs
     * @param articleRepository articles repository for the article order
     */
    public MailService(JavaMailSender mailSender, ArticleRepository articleRepository) {
        this.mailSender = mailSender;
        this.articleRepository = articleRepository;
    }

    /**
     * Sends a mail with attachment
     * @param to recipients
     * @param subject subject of the mail message
     * @param body body of the mail message
     * @param fileToAttach file to attach to mail
     */
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(new File(fileToAttach));
            String fileName = file.getFilename();
            if(fileName!=null){
                helper.addAttachment(fileName, file);
            }

            mailSender.send(mimeMessage);
            log.info("Mail was successfully sent!");
        } catch (MessagingException e) {
            log.error("An error occured while sending mail.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the csv for the order with the articles to order
     * @param articleMap a map with the articles for the order
     * @return path to csv file
     */
    public String createCsvOrderForMail(Map<String,String> articleMap) throws IOException {
        CsvCreator csvCreator = new CsvCreator();
        csvSavePath = csvSavePath.replace("\\",File.separator);
        return csvCreator.createCsv(articleMap,csvSavePath,"Article_Order.csv",fileCsvSeparator);
    }

    /**
     * Prepares the articles which should be put on the order
     * @param quantityLimit the quantity limit under which the articles should be ordered
     * @return articles for the order
     */
    public Map<String,String> prepareArticlesForCsvOrder(int quantityLimit) throws NoArticlesFoundForOrderException{
        List<Article> articleList = articleRepository.getAllArticlesWithLowQuantity(quantityLimit);
        LinkedHashMap<String,String> articleMap = new LinkedHashMap<>();
        articleMap.put("Article_No","Description");
        for (Article article : articleList){
            articleMap.put(String.valueOf(article.getId()),article.getDescription());
        }
        if(articleList.size()!=0){
            log.info("Articles were found for order!");
        }
        else{
            log.error("No Articles found to order!");
            throw new NoArticlesFoundForOrderException("No Articles found to order!");
        }
        return  articleMap;
    }


}
