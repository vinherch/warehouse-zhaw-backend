package com.example.warehousesystem.controller;

import com.example.warehousesystem.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
@TestPropertySource(properties={ "order.quantity.limit=250" , "order.customer.name=test"})
class MailControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private MailService mailService;

    @Test
    void gets_When_getAllCurrenciesIsCalled() throws Exception {
        Map<String, String> articleMap = new LinkedHashMap<>();
        articleMap.put("Testkey", "Testvalue");
        articleMap.put("Testkey2", "Testvalue2");
        when(mailService.prepareArticlesForCsvOrder(anyInt())).thenReturn(articleMap);
        doNothing().when(mailService).sendMailWithAttachment(anyString(), anyString(), anyString(), anyString());
        mvc.perform(get("/v1/mail")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}