package com.banking.exceptions.exps;



public class TransactionExceptions {
    public static class InsufficientBalanceException extends Exception {
        public InsufficientBalanceException(String s) {
            super(s);
        }
    }

    public static class NoTransactionPasswordException extends Exception {
        public NoTransactionPasswordException(String s) {
            super(s);
        }
    }

    public static class InvalidTransactionPasswordException extends Exception {
        public InvalidTransactionPasswordException(String s) {
            super(s);
        }
    }

}

