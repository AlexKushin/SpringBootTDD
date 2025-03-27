package com.okushyn.spring.tdd.workshop.exceptions;

public class ApplicantNotExistsException extends RuntimeException {

    public ApplicantNotExistsException() {}

    public ApplicantNotExistsException(String message) {
        super(message);
    }
}
