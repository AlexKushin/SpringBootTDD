package com.okushyn.spring.tdd.workshop.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    @NotEmpty
    @Pattern(regexp = "[a-zA-Z]+")
    private String lastName;
    private String middleName;
}