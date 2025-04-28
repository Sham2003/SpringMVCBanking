package com.banking.service;

import com.banking.dto.transaction.DepoWithdrawRequest;
import com.banking.dto.transaction.TransactionRequest;
import com.banking.dto.transaction.TransactionResponse;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.exceptions.exps.TransactionExceptions;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }

    public void saveTransaction(String accountNumber, String type, double amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public void depositWithdrawal(DepoWithdrawRequest depoWithdrawRequest) throws TransactionExceptions.InsufficientBalanceException {
        String transactionType = depoWithdrawRequest.getTransactionType();
        double amount = depoWithdrawRequest.getAmount();
        String accountNumber = depoWithdrawRequest.getAccountNumber();
        Account account = accountRepository.findByAccountNumber(depoWithdrawRequest.getAccountNumber());

        if (account == null) {
            throw new AuthExceptions.AccountNotFoundException("Account not found");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if ("deposit".equals(transactionType)) {
            account.setBalance(account.getBalance() + amount);
            saveTransaction(accountNumber, "deposit", amount, "Deposited into account");
        } else if ("withdraw".equals(transactionType)) {
            if (account.getBalance() < amount) {
                throw new TransactionExceptions.InsufficientBalanceException("Insufficient Balance !!!");
            }
            account.setBalance(account.getBalance() - amount);
            saveTransaction(accountNumber, "withdraw", amount, "Withdrawn from account");
        } else {
            throw new IllegalArgumentException("Invalid transaction type.");
        }

        accountRepository.save(account);

    }

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) throws TransactionExceptions.InsufficientBalanceException {
        Account senderAccount = accountRepository.findByAccountNumber(transactionRequest.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(transactionRequest.getReceiverAccountNumber());

        if (receiverAccount == null || senderAccount == null) {
            throw new AuthExceptions.AccountNotFoundException("Account not found");
        }

        if (transactionRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (senderAccount.getBalance() < transactionRequest.getAmount()) {
            throw new TransactionExceptions.InsufficientBalanceException("Insufficient Balance");
        }

        senderAccount.setBalance(senderAccount.getBalance() - transactionRequest.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + transactionRequest.getAmount());

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Save transaction records for both sender and receiver
        String senderAccountNumber = transactionRequest.getSenderAccountNumber();
        String receiverAccountNumber = transactionRequest.getReceiverAccountNumber();
        double amount = transactionRequest.getAmount();
        saveTransaction(senderAccountNumber, "transfer", amount, "Transferred to " + receiverAccountNumber);
        saveTransaction(receiverAccountNumber, "transfer", amount, "Received from " + senderAccountNumber);
        return TransactionResponse.builder()
                .message("Transaction Successful")
                .senderBalance(senderAccount.getBalance())
                .build();
    }
}
