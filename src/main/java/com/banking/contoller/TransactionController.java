package com.banking.contoller;


import com.banking.dto.auth.RegisterResponse;
import com.banking.dto.transaction.*;
import com.banking.exceptions.exps.TransactionExceptions;
import com.banking.exceptions.exps.TransactionExceptions.InsufficientBalanceException;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final UserService userService;

    private final AccountService accountService;

    @PostMapping("/initTransactionPassword")
    public ResponseEntity<String> initChangeTransactionPassword(@RequestParam("accountNumber") String accountNumber) {
        String otpReqId = accountService.initTransactionPassword(accountNumber);
        return ResponseEntity.ok().body(otpReqId);
    }

    @PostMapping("/changeTransactionPassword")
    public ResponseEntity<?> changeTransactionPassword(@RequestBody ResetTpwdDTO resetTpwdDTO) {
        accountService.changeTransactionPassword(resetTpwdDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/create-another-account",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> createAccount(@RequestParam("accountType") String accountType) {
        RegisterResponse response = accountService.createAnotherAccount(accountType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/accountDetails")
    public ResponseEntity<List<AccountInfo>> viewAccountDetails() {
        List<AccountInfo> response = userService.getAccountDetails();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accountNumbers")
    public ResponseEntity<List<String>> getAccountNumbers() {
        List<String> response = userService.getAccountNumbers();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/transactions/history",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionHistoryResponse> showTransactionHistoryPage(@RequestParam String accountNumber) {
        TransactionHistoryResponse response = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/bank-transfer",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest transactionRequest) throws InsufficientBalanceException, TransactionExceptions.NoTransactionPasswordException, TransactionExceptions.InvalidTransactionPasswordException {
        TransactionResponse transactionResponse = transactionService.processTransaction(transactionRequest);
        return ResponseEntity.ok().body(transactionResponse);
    }

    @PostMapping("/depo-withdraw")
    public ResponseEntity<?> processDepositWithdraw(@RequestBody DepoWithdrawRequest depoWithdrawRequest) throws InsufficientBalanceException {
        transactionService.depositWithdrawal(depoWithdrawRequest);
        return ResponseEntity.ok().build();
    }
}
