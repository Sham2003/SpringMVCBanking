package com.banking.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public enum ErrorCodes {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No Code"),
    INCORRECT_AGE(300, HttpStatus.BAD_REQUEST, "Incorrect Age"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    INVALID_FIELD(300, HttpStatus.BAD_REQUEST, "Invalid Field"),
    INVALID_OTP(300, HttpStatus.BAD_REQUEST, "Invalid OTP"),
    EXPIRED_OTP(300, HttpStatus.BAD_REQUEST, "Expired OTP"),
    WEAK_PASSWORD(300, HttpStatus.BAD_REQUEST, "Weak password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "New password does not match"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login or password is incorrect"),
    ACCOUNT_EXISTS(305, HttpStatus.FORBIDDEN, "Account already exists"),
    EXPIRED_SESSION(306, HttpStatus.FORBIDDEN, "Expired session. Please try again"),
    INVALID_SESSION(307, HttpStatus.FORBIDDEN, "Invalid session. Please login again"),
    INSUFFICIENT_FUNDS(400, HttpStatus.BAD_REQUEST, "Insufficient funds in the account"),
    UNAUTHORIZED_ACCESS(401, HttpStatus.UNAUTHORIZED, "Unauthorized access to resource"),
    USER_NOT_FOUND(402, HttpStatus.NOT_FOUND, "User not found"),
    NO_SUCH_REQUEST(404, HttpStatus.NOT_FOUND, "No such request"),
    ACCOUNT_NOT_FOUND(402, HttpStatus.NOT_FOUND, "Account not found"),
    LOAN_PENDING(402, HttpStatus.CONFLICT, "Loan Approver Pending"),
    MISSING_JWT(403, HttpStatus.FORBIDDEN, "Missing JWT"),
    NO_TRANSACTION_PASSWORD(403, HttpStatus.FORBIDDEN, "No transaction password"),
    INVALID_TRANSACTION_PASSWORD(403, HttpStatus.FORBIDDEN, "Invalid transaction password"),
    TRANSACTION_FAILED(403, HttpStatus.INTERNAL_SERVER_ERROR, "Transaction failed due to a system error");


    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    ErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
