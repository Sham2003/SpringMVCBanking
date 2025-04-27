package com.banking.exceptions.handlers;


import ai.onnxruntime.OrtException;
import com.banking.exceptions.ErrorCodes;
import com.banking.exceptions.ExceptionResponse;
import com.banking.exceptions.exps.TransactionExceptions.*;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class TransactionExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.INVALID_FIELD.getCode())
                                .serverErrorDescrtiption(ErrorCodes.INVALID_FIELD.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionResponse> handleException(InsufficientBalanceException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.INSUFFICIENT_FUNDS.getCode())
                                .serverErrorDescrtiption(ErrorCodes.INSUFFICIENT_FUNDS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(OrtException.class)
    public ResponseEntity<ExceptionResponse> handleException(OrtException exp) {
        System.out.println(exp.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.LOAN_PENDING.getCode())
                                .serverErrorDescrtiption(ErrorCodes.LOAN_PENDING.getDescription())
                                .error("Couldn't approve the transaction")
                                .build()
                );
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleException(ValidationException exp) {
        System.out.println(exp.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .serverErrorCode(ErrorCodes.INVALID_FIELD.getCode())
                                .serverErrorDescrtiption(ErrorCodes.INVALID_FIELD.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

}
