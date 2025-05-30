package com.banking.exceptions.handlers;


import com.banking.exceptions.ErrorCodes;
import com.banking.exceptions.ExceptionResponse;
import com.banking.exceptions.exps.AuthExceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
                                .serverErrorDescription(ErrorCodes.ACCOUNT_EXISTS.getDescription())
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
                                .serverErrorDescription(ErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidOtpException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.INVALID_OTP.getCode())
                                .serverErrorDescription(ErrorCodes.INVALID_OTP.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ExpiredOtpException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredOtpException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.EXPIRED_OTP.getCode())
                                .serverErrorDescription(ErrorCodes.EXPIRED_OTP.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(NoSuchRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(NoSuchRequestException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.NO_SUCH_REQUEST.getCode())
                                .serverErrorDescription(ErrorCodes.NO_SUCH_REQUEST.getDescription())
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
                                .serverErrorDescription(ErrorCodes.WEAK_PASSWORD.getDescription())
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
                                .serverErrorDescription(ErrorCodes.INCORRECT_AGE.getDescription())
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
                                .serverErrorDescription(ErrorCodes.USER_NOT_FOUND.getDescription())
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
                                .serverErrorDescription(ErrorCodes.ACCOUNT_NOT_FOUND.getDescription())
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
                                .serverErrorDescription(ErrorCodes.ACCOUNT_LOCKED.getDescription())
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
                                .serverErrorDescription(ErrorCodes.BAD_CREDENTIALS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleException(JwtException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setError(exp.getMessage());
        if (exp instanceof ExpiredJwtException ) {
            exceptionResponse.setServerErrorCode(ErrorCodes.EXPIRED_SESSION.getCode());
            exceptionResponse.setServerErrorDescription(ErrorCodes.EXPIRED_SESSION.getDescription());
        } else {
            exceptionResponse.setServerErrorCode(ErrorCodes.INVALID_SESSION.getCode());
            exceptionResponse.setServerErrorDescription(ErrorCodes.INVALID_SESSION.getDescription());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

}
