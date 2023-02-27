package com.example.warehousesystem.service;

import com.example.warehousesystem.controller.MailController;
import com.example.warehousesystem.exceptions.NoArticlesFoundForOrderException;
import com.example.warehousesystem.utils.HasLogger;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author yasmin.rosskopf
 * A component that is started at the startup of the application
 * It starts running a background thread to recurrently send an email
 */
@Component
public class MailTimer implements HasLogger {

    @Value("${mail.timer.period}")
    private String period;
    @Value("${mail.timer.delay}")
    private String delay;
    private final Logger logger = getLogger();
    private final MailController mailController;

    public MailTimer(MailController mailController) {
        this.mailController = mailController;
    }

    /**
     * After dependency injection is done and the values are read from the property file,
     * the method to start the thread can be called
     */
    @PostConstruct
    public void init() {
        this.scheduleRecurrently();
        logger.info("MailTimer started");
    }

    /**
     * starts the actual background thread to recurrently send an email
     */
    public void scheduleRecurrently() {
        TimerTask task = new TimerTask() {

            public void run() {
                try {
                    mailController.sendMail();
                    logger.info("Automatic mail was sent");
                } catch (IOException | NoArticlesFoundForOrderException e ) {
                    logger.error("mail was not sent due to " + e.getMessage());
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(task, Long.parseLong(delay), Long.parseLong(period));
    }
}