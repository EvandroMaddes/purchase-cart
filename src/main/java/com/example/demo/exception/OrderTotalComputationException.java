package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Error computing total order value")  // 404
public class OrderTotalComputationException extends Exception {

    public OrderTotalComputationException(String errorMessage) {
        super(errorMessage);
    }
}