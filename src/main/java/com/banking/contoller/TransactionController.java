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
        TransactionResponse transactionResponse = transactionService.processTransaction(transactionRequest);
        return ResponseEntity.ok().body(transactionResponse);
    }

    @PostMapping("/depo-withdraw")
    public ResponseEntity<?> processDepositWithdraw(@RequestBody DepoWithdrawRequest depoWithdrawRequest) throws InsufficientBalanceException {
        transactionService.depositWithdrawal(depoWithdrawRequest);
        return ResponseEntity.ok().build();
    }


}
