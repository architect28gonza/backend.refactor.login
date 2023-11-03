package com.refactor.login.exception;

import org.springframework.http.HttpStatus;

public class ExceptionMain extends Exception {
    
    private final HttpStatus status;

    public ExceptionMain(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
