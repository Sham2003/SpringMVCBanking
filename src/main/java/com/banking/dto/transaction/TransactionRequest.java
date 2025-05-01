package com.banking.dto.transaction;

import lombok.Data;

@Data
public class TransactionRequest {
    String senderAccountNumber;
    String receiverAccountNumber;
    double amount;
    String transactionPassword;
}
