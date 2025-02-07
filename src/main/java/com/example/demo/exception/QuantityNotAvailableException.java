package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No enough quantity")  // 400
public class QuantityNotAvailableException extends IllegalArgumentException {

    public QuantityNotAvailableException(String errorMessage) {
        super(errorMessage);
    }
}