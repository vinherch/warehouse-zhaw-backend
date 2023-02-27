package com.example.warehousesystem.service;

import com.example.warehousesystem.controller.MailController;
import com.example.warehousesystem.exceptions.NoArticlesFoundForOrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.Mockito.*;



/**
 * @author yasmin.rosskopf
 * Test class for the mailtimer
 */
@SpringBootTest(properties={"mail.timer.period=10000000", "mail.timer.delay=0"}, classes = MailTimer.class)
class MailTimerTest {

    @MockBean
    private MailController mailController;

    /**
     * Tests if the mailController is called to send an email
     * @throws IOException file related exceptions
     * @throws NoArticlesFoundForOrderException if no article is found for with the low quantity limit
     */
    @Test
    public void sendsMail_When_StartingApplication() throws IOException, NoArticlesFoundForOrderException {
        doNothing().when(mailController).sendMail();
        MailTimer mailTimer = new MailTimer(mailController);
        verify(mailController, times(1)).sendMail();
    }

    /**
     * Tests if no exception is thrown when the MailController throws one
     */
    @Test
    public void doesNotThrowException_When_MailControllerThrowsException() {
        Assertions.assertDoesNotThrow(() -> {
            Mockito.doThrow(new NoArticlesFoundForOrderException("TestArticle not found")).when(mailController).sendMail();
            MailTimer mailTimer = new MailTimer(mailController);
        });
    }
}