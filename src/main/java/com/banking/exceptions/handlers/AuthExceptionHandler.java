package com.banking.exceptions.handlers;


import com.banking.exceptions.ErrorCodes;
import com.banking.exceptions.ExceptionResponse;
import com.banking.exceptions.exps.AuthExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountLockedException;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountExistsException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.ACCOUNT_EXISTS.getCode())
                                .serverErrorDescrtiption(ErrorCodes.ACCOUNT_EXISTS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidPasswordException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH.getCode())
                                .serverErrorDescrtiption(ErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleException(WeakPasswordException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.WEAK_PASSWORD.getCode())
                                .serverErrorDescrtiption(ErrorCodes.WEAK_PASSWORD.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ExceptionResponse> handleException(SecurityException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.INCORRECT_AGE.getCode())
                                .serverErrorDescrtiption(ErrorCodes.INCORRECT_AGE.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.USER_NOT_FOUND.getCode())
                                .serverErrorDescrtiption(ErrorCodes.USER_NOT_FOUND.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.ACCOUNT_NOT_FOUND.getCode())
                                .serverErrorDescrtiption(ErrorCodes.ACCOUNT_NOT_FOUND.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountLockedException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.ACCOUNT_LOCKED.getCode())
                                .serverErrorDescrtiption(ErrorCodes.ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.BAD_CREDENTIALS.getCode())
                                .serverErrorDescrtiption(ErrorCodes.BAD_CREDENTIALS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }




}
