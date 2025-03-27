package com.okushyn.spring.tdd.workshop.service;

import com.okushyn.spring.tdd.workshop.exceptions.ApplicantAlreadyExistsException;
import com.okushyn.spring.tdd.workshop.exceptions.ApplicantNotExistsException;
import com.okushyn.spring.tdd.workshop.model.Applicant;
import com.okushyn.spring.tdd.workshop.model.ContactPoint;
import com.okushyn.spring.tdd.workshop.model.ElectronicAddress;
import com.okushyn.spring.tdd.workshop.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicantService {

    @Autowired
    ApplicantRepository repository;

    public Applicant save(Applicant applicant) {

        Optional<Applicant> byEmail = repository.findByEmail(Optional.of(applicant)
                .map(Applicant::getContactPoint)
                .map(ContactPoint::getElectronicAddress)
                .map(ElectronicAddress::getEmail)
                .orElseThrow());

        if (byEmail.isPresent()) {
            throw new ApplicantAlreadyExistsException("Applicant already exists");
        }

        return repository.save(applicant);
    }

    public Applicant getByEmail(String email) {

        Optional<Applicant> applicant = repository.findByEmail(email);

        if (applicant.isEmpty()) {
            throw new ApplicantNotExistsException("Applicant not with email " + email + " is unknown");
        }
        return applicant.get();
    }

    public Applicant getById(Long applicantId) {
        Optional<Applicant> applicant = repository.findById(applicantId);
        if (applicant.isEmpty()) {
            throw new ApplicantNotExistsException("Applicant with id " + applicantId + " is unknown");
        }
        return applicant.get();
    }

    public void deleteApplicantById(Long applicantId) {
        getById(applicantId);
        repository.deleteById(applicantId);
    }
}
