package com.Turfbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GeneralException extends RuntimeException {

    HttpStatus status;

    public GeneralException(String exceptionMessage, HttpStatus status) {
        super(exceptionMessage);
        this.status = status;

    }

    public HttpStatus getStatus() {
        return status;
    }
}

