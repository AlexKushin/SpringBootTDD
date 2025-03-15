package com.okushyn.spring.tdd.workshop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonName {
    private String firstName;
    private String lastName;
    private String middleName;
}