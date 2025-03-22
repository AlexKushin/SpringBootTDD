package com.okushyn.spring.tdd.workshop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicantAlreadyExistsException.class)
    protected ResponseEntity<Object> handleApplicantAlreadyExistsException(ApplicantAlreadyExistsException ex) {
        //todo: ApiError?
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApplicantNotExistsException.class)
    protected ResponseEntity<Object> handleApplicantNotExistsException(ApplicantNotExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
