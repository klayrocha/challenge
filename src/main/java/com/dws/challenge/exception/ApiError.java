package com.dws.challenge.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class ApiError implements Serializable {

    private static final long serialVersionUID = 8703468782453888331L;
    private HttpStatus status;
    private String message;

    ApiError(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.message = message;
    }
}