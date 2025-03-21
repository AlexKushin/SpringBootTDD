package com.okushyn.spring.tdd.workshop.repository;

import com.okushyn.spring.tdd.workshop.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByEmail(String email);
}
