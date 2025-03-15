package com.okushyn.spring.tdd.workshop.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Applicant {
    @Id
    @GeneratedValue
    private Long applicantId;

    @Valid
    @NotNull
    @Embedded
    private Person person;

    @Valid
    @NotNull
    @Embedded
    private ContactPoint contactPoint;
}