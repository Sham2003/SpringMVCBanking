package com.banking.dto.transaction;


import com.banking.model.Transaction;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class TransactionHistoryItem{
    private String accountNumber;           // General reference for querying
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String type;                    // "deposit", "withdraw", "transfer"
    private double amount;
    private String description;
    private LocalDateTime timestamp;
}

@AllArgsConstructor
public class TransactionHistoryResponse {
    List<TransactionHistoryItem> transactionHistory;
    int totalTransactions;

    public static TransactionHistoryResponse fromList(List<Transaction> rawTransactions) {
        List<TransactionHistoryItem> transactionHistory = new ArrayList<>();
        for (Transaction transaction : rawTransactions) {
            TransactionHistoryItem transactionHistoryItem = new TransactionHistoryItem();
            transactionHistory.add(transactionHistoryItem);
        }
        return new TransactionHistoryResponse(transactionHistory, rawTransactions.size());
    }
}
