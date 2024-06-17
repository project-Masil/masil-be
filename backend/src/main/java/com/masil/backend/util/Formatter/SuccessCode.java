package com.masil.backend.util.Formatter;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum SuccessCode implements ResponseCode {

    SUCCESS(HttpStatus.OK, "success!");


    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getCode() {
        return name();
    }

}
