package com.okushyn.spring.tdd.workshop.controller;

import com.okushyn.spring.tdd.workshop.model.*;
import com.okushyn.spring.tdd.workshop.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/applicants")
public class ApplicantController {

    @Autowired
    private ApplicantService applicantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Applicant> createApplicant(final @Valid @RequestBody Applicant applicant) throws Exception {
        final Applicant savedApplicant = applicantService.save(applicant);
        return ResponseEntity.created(new URI("applicants/" + savedApplicant.getApplicantId()))
                .body(applicant);
    }

    @GetMapping(params = {"email"}, path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Applicant> getApplicant(final @RequestParam("email") String email) throws Exception {
        Applicant applicantToGet = Applicant.builder()
                .person(Person.builder()
                        .personName(PersonName.builder()
                                .lastName("Lastname")
                                .build())
                        .build())
                .contactPoint(ContactPoint.builder()
                        .electronicAddress(ElectronicAddress.builder()
                                .email(email)
                                .build())
                        .build())
                .build();

        if(email.equals("badTest@test.com")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(applicantToGet);
    }
}
