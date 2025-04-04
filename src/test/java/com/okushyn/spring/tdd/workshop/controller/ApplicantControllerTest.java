package com.okushyn.spring.tdd.workshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okushyn.spring.tdd.workshop.exceptions.ApplicantAlreadyExistsException;
import com.okushyn.spring.tdd.workshop.exceptions.ApplicantNotExistsException;
import com.okushyn.spring.tdd.workshop.model.*;
import com.okushyn.spring.tdd.workshop.service.ApplicantService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
        AssertionsForClassTypes.assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("When a valid name and email are provided, then ID of created record should be returned ")
    void createApplicant_whenValidEmailAndLastNameProvidedThenReturnId() throws Exception {
        Applicant originalApplicant = getApplicantWithLastnameAndElectronicAddressForTest();
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
                .andExpect(header().string(HttpHeaders.LOCATION, "applicants/" + applicantId))
                //checking json body
                .andExpect(jsonPath("$.applicantId", equalTo(7)))
                .andExpect(jsonPath("$.person.personName.lastName", equalTo("Lastname")));

        //to be sure that Applicant which I sent to controller actually the same as I received in ApplicantService
        final ArgumentCaptor<Applicant> applicantArgumentCaptor = ArgumentCaptor.forClass(Applicant.class);

        verify(applicantService, times(1)).save(applicantArgumentCaptor.capture());

        final Applicant capturedApplicant = applicantArgumentCaptor.getValue();

        assertThat(capturedApplicant)
                .usingRecursiveComparison()
                .ignoringFields("applicantId") //in this place we don't need to compare id
                .isEqualTo(originalApplicant);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void createApplicant_shouldFailIfEmailOrLastNameNotProvided(final Applicant applicant) throws Exception {
        when(applicantService.save(any(Applicant.class))).thenAnswer(invocation -> {
            final Applicant app = invocation.getArgument(0, Applicant.class);
            app.setApplicantId(7L);
            return app;
        });

        mockMvc.perform(post("/applicants")
                        .content(objectMapper.writeValueAsString(applicant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    public static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(
                        Applicant.builder()
                                .build()),
                Arguments.of(
                        Applicant.builder()
                                .person(Person.builder()
                                        .personName(PersonName.builder()
                                                .lastName("Lastname")
                                                .build())
                                        .build())
                                .build()
                ),
                Arguments.of(Applicant.builder()
                        .contactPoint(ContactPoint.builder()
                                .electronicAddress(ElectronicAddress.builder()
                                        .email("test@test.com")
                                        .build())
                                .build())
                        .build())
        );

    }

    @Test
    void createApplicant_shouldReturn409WhenApplicantExists() throws Exception {

        Applicant applicant = getApplicantWithLastnameAndElectronicAddressForTest();

        when(applicantService.save(any(Applicant.class))).thenThrow(ApplicantAlreadyExistsException.class);
        //2-nd - Act
        mockMvc.perform(
                        //1-st A - Arrange
                        post("/applicants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(applicant))
                )
                //3-rd A - Assert
                .andExpect(status().isConflict());

        verify(applicantService, times(1)).save(any(Applicant.class));
    }

    @Test
    @DisplayName("When a valid email is provided, then Applicant record should be returned ")
    void getApplicantByEmail_whenValidEmailThenReturnApplicant() throws Exception {
        String appEmail = "test@test.com";
        Applicant applicantToGet = getApplicantWithLastnameAndElectronicAddressForTest();
        when(applicantService.getByEmail(appEmail)).thenAnswer(invocation -> applicantToGet);

        mockMvc.perform(
                        //1st A - Arrange
                        get("/applicants")
                                .param("email", appEmail)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                //3rd A - Assert
                .andExpect(status().isOk())
                //checking json body
                .andExpect(jsonPath("$.contactPoint.electronicAddress.email", equalTo("test@test.com")));

        final ArgumentCaptor<String> emailArgumentCaptor = ArgumentCaptor.forClass(String.class);


        verify(applicantService, times(1)).getByEmail(emailArgumentCaptor.capture());

        final String capturedEmail = emailArgumentCaptor.getValue();

        assertThat(capturedEmail).isEqualTo(appEmail);


    }

    @Test
    @DisplayName("When an unknown to bank email is provided, then Status Code 404")
    void getApplicantByEmail_shouldReturn404IfApplicantNotExists() throws Exception {
        String appEmail = "badTest@test.com";
        when(applicantService.getByEmail(any(String.class))).thenThrow(ApplicantNotExistsException.class);

        mockMvc.perform(
                        get("/applicants")
                                .param("email", appEmail)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        final ArgumentCaptor<String> emailArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(applicantService, times(1)).getByEmail(emailArgumentCaptor.capture());

        final String capturedEmail = emailArgumentCaptor.getValue();

        assertThat(capturedEmail).isEqualTo(appEmail);
    }


    @Test
    @DisplayName("When a known to bank Applicant Id is provided, then Applicant record should be returned ")
    void getApplicantById_whenValidIdThenReturnApplicant() throws Exception {
        long applicantId = 7L;

        when(applicantService.getById(applicantId)).thenAnswer(invocation -> {
            final Applicant applicantToReturn = getApplicantWithLastnameAndElectronicAddressForTest();

            applicantToReturn.setApplicantId(applicantId);
            return Optional.of(applicantToReturn);
        });

        mockMvc.perform(
                        get("/applicants/" + applicantId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicantId", equalTo((int) applicantId)));

        verify(applicantService, times(1)).getById(anyLong());

    }

    @Test
    @DisplayName("When an unknown to bank Applicant id is provided, then Status Code 404")
    void getApplicantById_shouldReturn404IfApplicantNotExists() throws Exception {
        when(applicantService.getById(anyLong())).thenThrow(ApplicantNotExistsException.class);

        mockMvc.perform(
                        get("/applicants/" + anyLong())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(applicantService, times(1)).getById(anyLong());


    }

    @Test
    @DisplayName("When a valid Applicant Id is provided, then Applicant record should be removed ")
    void deleteApplicantById_whenKnownIdThenReturnApplicant() throws Exception {
        long applicantId = 7L;


        mockMvc.perform(
                        delete("/applicants/" + applicantId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());


        final ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(applicantService, times(1)).deleteApplicantById(idArgumentCaptor.capture());

        final Long capturedApplicantId = idArgumentCaptor.getValue();

        assertThat(capturedApplicantId).isEqualTo(applicantId);
    }

    @Test
    @DisplayName("When an unknown to bank Applicant id is provided, then Status Code 404")
    void deleteApplicantById_shouldReturn404IfApplicantNotExists() throws Exception {
        doThrow(new ApplicantNotExistsException()).when(applicantService).deleteApplicantById(anyLong());

        mockMvc.perform(
                        delete("/applicants/" + anyLong())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(applicantService, times(1)).deleteApplicantById(anyLong());

    }




    private Applicant getApplicantWithLastnameAndElectronicAddressForTest() {
        return Applicant.builder()
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
    }

}