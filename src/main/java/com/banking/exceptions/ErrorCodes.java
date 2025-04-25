package com.banking.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCodes {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No Code"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "New password does not match"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "USer account is locked"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "USer account is disabled"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login or password is incorrect");

    @Getter
    private int code;
    @Getter
    private String description;
    @Getter
    private HttpStatus httpStatus;

    ErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
