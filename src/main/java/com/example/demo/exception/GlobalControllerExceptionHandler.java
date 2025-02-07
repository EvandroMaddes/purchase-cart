package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(exception = {DataIntegrityViolationException.class, QuantityNotAvailableException.class, OrderTotalComputationException.class})
    public void handleConflict() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(exception = {ProductNotFoundException.class})
    public void handleNotFound() {
        // Nothing to do
    }

}
