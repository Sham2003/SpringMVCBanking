package com.banking.dto.transaction;

import lombok.Data;

@Data
public class DepoWithdrawRequest {
    String accountNumber;
    String transactionType;
    double amount;
}
