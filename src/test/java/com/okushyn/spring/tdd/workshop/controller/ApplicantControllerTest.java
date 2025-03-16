package com.okushyn.spring.tdd.workshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okushyn.spring.tdd.workshop.model.*;
import com.okushyn.spring.tdd.workshop.service.ApplicantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@WebMvcTest
class ApplicantControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApplicantService applicantService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void check_contextStarts() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("When a valid name and email are provided, then ID of created record should be returned ")
    void createApplicant_whenValidEmailAndLastNameProvidedThenReturnId() throws Exception {
        Applicant originalApplicant = Applicant.builder()
                .person(Person.builder()
                        .personName(PersonName.builder()
                                .lastName("Lastname")
                                .build())
                        .build())
                .contactPoint(ContactPoint.builder()
                        .electronicAddress(ElectronicAddress.builder()
                                .email("test@test.com")
                                .build())
                        .build())
                .build();
        //here I ask my mock of applicant service to answer me with Applicant object and set its id
        final Long applicantId = 7L;

        when(applicantService.save(any(Applicant.class))).thenAnswer(invocation -> {
            final Applicant argument = invocation.getArgument(0, Applicant.class);
            argument.setApplicantId(applicantId);
            return argument;
        });

        //simulating a behavior when request is sent to particular endpoint
        //2nd A- Act
        mockMvc.perform(
                        //1st A - Arrange
                        post("/applicants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(originalApplicant))
                )
                //3rd A - Assert
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "applicants/" + applicantId));

        //to be sure that Applicant which I sent to controller actually the same as I received in ApplicantService
        final ArgumentCaptor<Applicant> applicantArgumentCaptor = ArgumentCaptor.forClass(Applicant.class);

        verify(applicantService, times(1)).save(applicantArgumentCaptor.capture());

        final Applicant capturedApplicant = applicantArgumentCaptor.getValue();

        assertThat(capturedApplicant)
                .usingRecursiveComparison()
                .ignoringFields("applicantId") //in this place we don't need to compare id
                .isEqualTo(originalApplicant);
    }

}