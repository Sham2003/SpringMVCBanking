package com.banking.repository;

import com.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);

    default void saveTransaction(String accountNumber, String type, double amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        save(transaction);
    }
}
