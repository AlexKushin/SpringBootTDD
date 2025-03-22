package com.okushyn.spring.tdd.workshop.service;

import com.okushyn.spring.tdd.workshop.exceptions.ApplicantAlreadyExistsException;
import com.okushyn.spring.tdd.workshop.model.Applicant;
import com.okushyn.spring.tdd.workshop.model.ContactPoint;
import com.okushyn.spring.tdd.workshop.model.ElectronicAddress;
import com.okushyn.spring.tdd.workshop.repository.ApplicantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(classes = {ApplicantService.class}) //we ask spring to create real Applicant service
class ApplicantServiceTest {

    @Autowired
    ApplicantService applicantService; //uut - unit under test

    @MockitoBean
    ApplicantRepository applicantRepository;

    @Test
    void check_contextStart() { // help us understand whether we included all necessary classes to application context
        assertThat(applicantService).isNotNull();
    }

    @Test
    void save_shouldReturnApplicant() {
        when(applicantRepository.findByEmail(eq("test@test.com"))).thenReturn(Optional.empty());

        final Applicant applicant = Applicant.builder()
                .contactPoint(ContactPoint.builder()
                        .electronicAddress(ElectronicAddress.builder()
                                .email("test@test.com")
                                .build())
                        .build())
                .build();

        when(applicantRepository.save(any(Applicant.class))).thenAnswer(inv -> {
            final Applicant toSave = inv.getArgument(0);
            toSave.setApplicantId(10L);
            return toSave;
            });

        final Applicant savedApplicant = applicantService.save(applicant);

        assertThat(savedApplicant)
                .isNotNull()
                .withFailMessage("Should not be null"); //customize the failure message when an assertion fails

        assertThat(savedApplicant)
                .extracting(Applicant::getApplicantId)
                .isNotNull()
                .withFailMessage("ApplicantId is null");

        assertThat(savedApplicant)
                .usingRecursiveComparison()
                .ignoringFields("applicantId")
                .isEqualTo(applicant)
                .withFailMessage("Saved applicant is not the same");

        verify(applicantRepository, times(1)).save(eq(applicant));

    }


    @Test
    void save_shouldThrowExceptionIfApplicantIsAlreadyExist() {
        when(applicantRepository.findByEmail(eq("test@test.com"))).thenReturn(Optional.of(Applicant.builder().build()));

        final Applicant applicant = Applicant.builder()
                .contactPoint(ContactPoint.builder()
                        .electronicAddress(ElectronicAddress.builder()
                                .email("test@test.com")
                                .build())
                        .build())
                .build();

        assertThatThrownBy(() -> {
            applicantService.save(applicant);
        })
                .isInstanceOf(ApplicantAlreadyExistsException.class);
    }

}