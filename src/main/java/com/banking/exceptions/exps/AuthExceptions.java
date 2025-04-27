package com.banking.exceptions.exps;



public class AuthExceptions {
    public static class AccountExistsException extends RuntimeException {
        public AccountExistsException(String message) {
            super(message);
        }
    }
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }
    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) { super(message); }
    }
    public static class WeakPasswordException extends RuntimeException {
        public WeakPasswordException(String message) { super(message); }
    }
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidOtpException extends RuntimeException {
        public InvalidOtpException(String message) { super(message); }
    }

    public static class ExpiredOtpException extends RuntimeException {
        public ExpiredOtpException(String message) { super(message); }
    }

    public static class NoSuchRequestException extends RuntimeException {
        public NoSuchRequestException(String message) { super(message); }
    }

}

