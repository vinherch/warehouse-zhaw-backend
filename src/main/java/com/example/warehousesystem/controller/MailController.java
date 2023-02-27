package com.example.warehousesystem.controller;

import com.example.warehousesystem.exceptions.NoArticlesFoundForOrderException;
import com.example.warehousesystem.service.MailService;
import com.example.warehousesystem.utils.HasLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * @author dejan.kosic
 * Controller class for the mail endpoint to send email on request
 */
@RestController
@RequestMapping("/v1/mail")
public class MailController implements HasLogger {

    private final Logger logger = getLogger();
    private final MailService mailService;

    @Value("${order.quantity.limit}")
    private int quantityLimit;
    @Value("${order.customer.name}")
    private String customerName;
    @Value("${order.customer.email}")
    private String email;

    /**
     * Constructor for MailController
     * @param mailService mail service to be used
     */
    public MailController(MailService mailService){
        this.mailService = mailService;
    }

    /**
     * Sends mail with a message and with an attachment
     * @throws IOException file related exceptions
     * @throws NoArticlesFoundForOrderException if no article is found for with the low quantity limit
     */
    @GetMapping
    public void sendMail() throws IOException, NoArticlesFoundForOrderException {
        Map<String,String> articleMap = mailService.prepareArticlesForCsvOrder(quantityLimit);
        mailService.createCsvOrderForMail(articleMap);
        if (articleMap.size() > 1) {
            logger.info("sending mail to "+ email);
            mailService.sendMailWithAttachment(email,"Artikelbestellung","Sehr geehrte " +
                    "Damen und Herren"+"\n\n"+"Im Anhang finden Sie unsere Bestellung für jeweils 100 Stk."+"\n\n"+
                    "Vielen Dank und freundliche Grüsse"+
                    "\n"+customerName,mailService.createCsvOrderForMail(articleMap));
        }
    }
}
