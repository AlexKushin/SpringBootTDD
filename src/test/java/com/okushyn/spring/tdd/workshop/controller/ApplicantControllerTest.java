package com.okushyn.spring.tdd.workshop.controller;

import com.okushyn.spring.tdd.workshop.model.Applicant;
import com.okushyn.spring.tdd.workshop.service.ApplicantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@WebMvcTest
class ApplicantControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApplicantService applicantService;

    @Test
    void check_contextStarts() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("When a valid name and email are provided, then ID of created record should be returned ")
    void createApplicant_whenValidEmailAndLastNameProvidedThenReturnId() throws Exception {
        //simulating a behavior when request is sent to particular endpoint
        //2nd A- Act
        mockMvc.perform(
                //1st A - Arrange
                post("/applicants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("") // we sent just an empty string to controller? and it is a reason why we have response 400
                )
                //3rd A - Assert
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "applicants/777"));

       verify(applicantService, times(1)).save(any(Applicant.class));
    }

}