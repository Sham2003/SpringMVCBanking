package com.banking.dto.transaction;


import com.banking.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
class TransactionHistoryItem{
    public Long id;
    private String accountNumber;           // General reference for querying
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String type;                    // "deposit", "withdraw", "transfer"
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public TransactionHistoryItem(Transaction transaction){
        this.id = transaction.getId();
        this.accountNumber = transaction.getAccountNumber();
        this.senderAccountNumber = transaction.getSenderAccountNumber();
        this.receiverAccountNumber = transaction.getReceiverAccountNumber();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.timestamp = transaction.getTimestamp();
    }
}

@AllArgsConstructor
@Data
public class TransactionHistoryResponse {
    List<TransactionHistoryItem> transactionHistory;
    int totalTransactions;

    public static TransactionHistoryResponse fromList(List<Transaction> rawTransactions) {
        List<TransactionHistoryItem> transactionHistory = new ArrayList<>();
        for (Transaction transaction : rawTransactions) {
            TransactionHistoryItem transactionHistoryItem = new TransactionHistoryItem(transaction);
            transactionHistory.add(transactionHistoryItem);
        }
        return new TransactionHistoryResponse(transactionHistory, rawTransactions.size());
    }
}
