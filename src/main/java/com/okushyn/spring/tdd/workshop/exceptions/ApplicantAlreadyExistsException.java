package com.okushyn.spring.tdd.workshop.exceptions;

public class ApplicantAlreadyExistsException extends RuntimeException{

    public ApplicantAlreadyExistsException(String message) {
        super(message);
    }
}
