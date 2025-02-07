package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(exception = {DataIntegrityViolationException.class, QuantityNotAvailableException.class, OrderTotalComputationException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        logErrorMessage(ex);
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(exception = {ProductNotFoundException.class})
    public ResponseEntity<String> handleNotFound(Exception ex) {
        logErrorMessage(ex);
        return ResponseEntity
                .notFound()
                .build();
    }

    private void logErrorMessage(Exception ex) {
        log.error(ex.getLocalizedMessage());
    }
}
