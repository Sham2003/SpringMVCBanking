package com.banking.contoller;


import com.banking.dto.transaction.*;
import com.banking.exceptions.exps.TransactionExceptions.InsufficientBalanceException;
import com.banking.model.Transaction;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
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
    private UserService userService;

    @PostMapping("/accountDetails")
    public ResponseEntity<List<AccountInfo>> viewAccountDetails(@RequestParam String email) {
        List<AccountInfo> response = userService.getAccountDetails(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accountNumbers")
    public ResponseEntity<List<String>> getAccountNumbers(@RequestParam String email) {
        List<String> response = userService.getAccountNumbers(email);
        return ResponseEntity.ok(response);
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
