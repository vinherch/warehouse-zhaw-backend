package com.example.warehousesystem.controller;

import com.example.warehousesystem.entities.Status;
import com.example.warehousesystem.exceptions.RecordAlreadyExistsException;
import com.example.warehousesystem.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.warehousesystem.utils.TestHelperMethods.asJson;
import static com.example.warehousesystem.utils.TestHelperMethods.createDummyStatus;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yasmin.rosskopf
 * Test for controller class for entity "status"
 * following <a href="https://spring.io/guides/gs/testing-web/">Spring guide</a>
 */
@WebMvcTest(StatusController.class)
public class StatusControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private StatusService statusService;
    private Status status;

    /**
     * instantiates up a dummy status to test on before each test
     */
    @BeforeEach
    public void setup() {
        status = createDummyStatus();
    }


    /**
     * Test for GET endpoint to fetch all statuss
     * @result returns a response with 200 OK, body with list of statuss
     */
    @Test
    public void getsResponseWithListOfAllCurrencies_When_getAllCurrenciesIsCalled() throws Exception {
        List<Status> statusList = new ArrayList<>();
        statusList.add(status);
        when(statusService.getAllStatus()).thenReturn(statusList);
        mvc.perform(get("/v1/statuses")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(status.getDescription())));
    }


    /**
     * Test for GET endpoint to an status by ID
     * @result returns a response with 200 OK, body with status with specific id
     */
    @Test
    public void getsResponseWithStatusWithSpecificId_When_getStatusByIdIsCalled() throws Exception {
        when(statusService.getStatusById(1)).thenReturn(status);
        mvc.perform(get("/v1/statuses/" + status.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(status.getDescription())));
    }


    /**
     * Test for DELETE endpoint to delete an status by ID
     * @result returns a response with 204 NO_CONTENT, and calls statusService to delete status
     */
    @Test
    public void getsResponseWithNoContent_When_deleteStatusByIdIsCalled() throws Exception {
        doNothing().when(statusService).deleteStatusById(1L);
        mvc.perform(delete("/v1/statuses/" + status.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }


    /**
     * Test for POST endpoint to add a new status
     * @result returns a response with 201 CREATED and body with the new status
     */
    @Test
    public void getsResponseWithStatusWithId_When_addStatusIsCalled() throws Exception {
        when(statusService.addStatus(status)).thenReturn(status);
        mvc.perform(post("/v1/statuses").content(asJson(status)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }


    /**
     * Test for POST endpoint to add a new status
     * @result throws a RecordAlreadyExistsException
     */
    @Test
    public void throwsRecordAlreadyExistsException_When_addStatusIsCalled() throws Exception {
        //when
        when(statusService.addStatus(status)).thenThrow(new RecordAlreadyExistsException("Record already exists"));
        //then
        mvc.perform(
                        MockMvcRequestBuilders.post("/v1/statuses")
                                .content(asJson(status))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }


    /**
     * Test for PUT endpoint to modify an existing status
     * @result returns a response with 200 OK
     */
    @Test
    public void getsResponseWithStatusWithSpecificId_When_modifyStatusByIdIsCalled() throws Exception {
        doNothing().when(statusService).modifyStatusById(status, 1L);
        mvc.perform(put("/v1/statuses/" + status.getId()).content(asJson(status)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

}
