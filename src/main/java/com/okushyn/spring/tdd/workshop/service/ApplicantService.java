package com.okushyn.spring.tdd.workshop.service;

import com.okushyn.spring.tdd.workshop.exceptions.ApplicantAlreadyExistsException;
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

        return Applicant.builder().build();
    }
}
