package com.banking.contoller;


import com.banking.dto.transaction.*;
import com.banking.exceptions.exps.AuthExceptions.*;
import com.banking.exceptions.exps.TransactionExceptions.InsufficientBalanceException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.service.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;


    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/view-account")
    public ResponseEntity<AccountInfo> viewAccountDetails(@RequestParam String email) {
        String jpql = "SELECT u, a FROM User u LEFT JOIN Account a ON u.email = a.email WHERE u.email = :email";
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter("email", email)
                .getResultList();

        if (results.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Object[] result = results.get(0);
        Account account = (Account) result[1];


        return ResponseEntity.ok(new AccountInfo(account));
    }

    @PostMapping(value = "/transaction-history",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionHistoryResponse> showTransactionHistoryPage(@RequestParam String accountNumber) {
        if(accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);

        return ResponseEntity.ok().body(TransactionHistoryResponse.fromList(transactions));
    }

    @PostMapping(value = "/bank-transfer",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest transactionRequest) throws InsufficientBalanceException {

        Account senderAccount = accountRepository.findByAccountNumber(transactionRequest.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(transactionRequest.getReceiverAccountNumber());

        if (receiverAccount == null || senderAccount == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (transactionRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (senderAccount.getBalance() < transactionRequest.getAmount()) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }

        senderAccount.setBalance(senderAccount.getBalance() - transactionRequest.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + transactionRequest.getAmount());

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Save transaction records for both sender and receiver
        String senderAccountNumber = transactionRequest.getSenderAccountNumber();
        String receiverAccountNumber = transactionRequest.getReceiverAccountNumber();
        double amount = transactionRequest.getAmount();
        transactionService.saveTransaction(senderAccountNumber, "transfer", amount, "Transferred to " + receiverAccountNumber);
        transactionService.saveTransaction(receiverAccountNumber, "transfer", amount, "Received from " + senderAccountNumber);

        return ResponseEntity.ok().body(
                TransactionResponse
                        .builder()
                        .message("Transfer successful")
                        .senderBalance(senderAccount.getBalance())
                        .build()
        );
    }

    @PostMapping("/depo-withdraw")
    public ResponseEntity<?> processDepositWithdraw(@RequestBody DepoWithdrawRequest depoWithdrawRequest) throws InsufficientBalanceException {
        String transactionType = depoWithdrawRequest.getTransactionType();
        double amount = depoWithdrawRequest.getAmount();
        String accountNumber = depoWithdrawRequest.getAccountNumber();
        Account account = accountRepository.findByAccountNumber(depoWithdrawRequest.getAccountNumber());

        if (account == null) {
           throw new AccountNotFoundException("Account not found");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if ("deposit".equals(transactionType)) {
            account.setBalance(account.getBalance() + amount);
            transactionService.saveTransaction(accountNumber, "deposit", amount, "Deposited into account");
        } else if ("withdraw".equals(transactionType)) {
            if (account.getBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient Balance !!!");
            }
            account.setBalance(account.getBalance() - amount);
            transactionService.saveTransaction(accountNumber, "withdraw", amount, "Withdrawn from account");
        } else {
            throw new IllegalArgumentException("Invalid transaction type.");
        }

        accountRepository.save(account);
        return ResponseEntity.ok().build();
    }


}
