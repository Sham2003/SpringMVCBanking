package com.banking.contoller;


import com.banking.dto.transaction.AccountInfo;
import com.banking.dto.transaction.TransactionHistoryResponse;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/view-account")
    public ResponseEntity<AccountInfo> viewAccountDetails(@RequestParam("email") String email) {
        String jpql = "SELECT u, a FROM User u LEFT JOIN Account a ON u.email = a.email WHERE u.email = :email";
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter("email", email)
                .getResultList();

        if (results.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Object[] result = results.get(0);
        User user = (User) result[0];
        Account account = (Account) result[1];


        return ResponseEntity.ok(new AccountInfo(account));
    }

    @GetMapping("/transaction-history")
    public ResponseEntity<TransactionHistoryResponse> showTransactionHistoryPage(@RequestParam(value = "accountNumber", required = false) String accountNumber) {
        if(accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);

        return ResponseEntity.ok().body(TransactionHistoryResponse.fromList(transactions));
    }

}
