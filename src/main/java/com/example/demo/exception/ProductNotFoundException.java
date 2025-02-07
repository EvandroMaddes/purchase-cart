package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such product")  // 404
public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}