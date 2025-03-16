package com.okushyn.spring.tdd.workshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@WebMvcTest
class ApplicantControllerTest {

    @Autowired
    MockMvc mockMvc;

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
                        .content(" ")
                )
                //3rd A - Assert
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "applicants/777"));

    }

}