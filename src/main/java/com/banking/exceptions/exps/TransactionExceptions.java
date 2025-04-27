package com.banking.exceptions.exps;



public class TransactionExceptions {
    public static class InsufficientBalanceException extends Exception {
        public InsufficientBalanceException(String s) {
            super(s);
        }
    }
}

