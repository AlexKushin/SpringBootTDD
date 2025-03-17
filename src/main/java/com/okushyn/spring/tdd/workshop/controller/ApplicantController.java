package com.okushyn.spring.tdd.workshop.controller;

import com.okushyn.spring.tdd.workshop.model.Applicant;
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
    public ResponseEntity<Void> createApplicant(final @Valid @RequestBody Applicant applicant) throws Exception {
        final Applicant savedApplicant = applicantService.save(applicant);
        return ResponseEntity.created(new URI("applicants/" + savedApplicant.getApplicantId())).build();
    }
}
