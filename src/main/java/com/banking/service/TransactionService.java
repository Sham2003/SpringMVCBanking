package com.banking.service;

import com.banking.dto.transaction.DepoWithdrawRequest;
import com.banking.dto.transaction.TransactionHistoryResponse;
import com.banking.dto.transaction.TransactionRequest;
import com.banking.dto.transaction.TransactionResponse;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.exceptions.exps.TransactionExceptions;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private static User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public TransactionHistoryResponse getTransactionsByAccountNumber(String accountNumber) {
        User user = getAuthenticatedUser();
        boolean accountExists = user.getAccounts().stream().map(Account::getAccountNumber).anyMatch(accountNumber::equals);
        if (!accountExists) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + accountNumber);
        }
        List<Transaction> transactions = transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
        return TransactionHistoryResponse.fromList(transactions);
    }


    public void depositWithdrawal(DepoWithdrawRequest depoWithdrawRequest) throws TransactionExceptions.InsufficientBalanceException {
        String transactionType = depoWithdrawRequest.getTransactionType();
        double amount = depoWithdrawRequest.getAmount();
        String accountNumber = depoWithdrawRequest.getAccountNumber();
        User user = getAuthenticatedUser();
        Optional<Account> result = user.getAccounts().stream().filter(account1 -> account1.getAccountNumber().equals(accountNumber)).findFirst();
        if (result.isEmpty()) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + accountNumber);
        }
        Account account = result.get();
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        if ("deposit".equals(transactionType)) {
            account.setBalance(account.getBalance() + amount);
            transactionRepository.saveTransaction(accountNumber, "deposit", amount, "Deposited into account");
        } else if ("withdraw".equals(transactionType)) {
            if (account.getBalance() < amount)
                throw new TransactionExceptions.InsufficientBalanceException("Insufficient Balance !!!");
            account.setBalance(account.getBalance() - amount);
            transactionRepository.saveTransaction(accountNumber, "withdraw", amount, "Withdrawn from account");
        } else
            throw new IllegalArgumentException("Invalid transaction type.");
        accountRepository.save(account);
    }

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) throws TransactionExceptions.InsufficientBalanceException, TransactionExceptions.NoTransactionPasswordException, TransactionExceptions.InvalidTransactionPasswordException {
        Account senderAccount = accountRepository.findByAccountNumber(transactionRequest.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(transactionRequest.getReceiverAccountNumber());
        User user = getAuthenticatedUser();
        Hibernate.initialize(user.getAccounts());
        boolean isUserAccount = user.getAccounts()
                .stream()
                .anyMatch(account1 -> account1.getAccountNumber().equals(senderAccount.getAccountNumber()));
        if(!isUserAccount) {
            throw new AuthExceptions.AccountNotFoundException("User Account not found : " + transactionRequest.getSenderAccountNumber());
        }
        if (receiverAccount == null) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + transactionRequest.getReceiverAccountNumber());
        }

        if(senderAccount.getTransactionPassword() == null){
            throw new TransactionExceptions.NoTransactionPasswordException("First set the transaction password");
        }

        if(!senderAccount.getTransactionPassword().equals(transactionRequest.getTransactionPassword())){
            throw new TransactionExceptions.InvalidTransactionPasswordException("Invalid transaction password");
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
        transactionRepository.saveTransaction(senderAccountNumber, "transfer", amount, "Transferred to " + receiverAccountNumber);
        transactionRepository.saveTransaction(receiverAccountNumber, "transfer", amount, "Received from " + senderAccountNumber);
        return TransactionResponse.builder()
                .message("Transaction Successful")
                .senderBalance(senderAccount.getBalance())
                .build();
    }
}
