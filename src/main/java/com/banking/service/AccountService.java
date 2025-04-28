package com.banking.service;


import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.model.Account;
import com.banking.model.PendingAccount;

import java.util.List;

public interface AccountService {
    // Method to get an account by email
    List<Account> getAccountByEmail(String email);
    String createPendingAccount(RegisterAccountDTO newAccount);

    PendingAccount findPendingAccount(String email);

    RegisterResponse verifyPendingAccount(String email, String otpReqId, String otp);

    RegisterResponse registerAccount(RegisterAccountDTO registerAccountDTO);

    RegisterResponse createAnotherAccount(String email,String accountType);

    void changeTransactionPassword(String email,String accountNumber, String otpReqId, String otp, String transactionPassword);

    String initTransactionPassword(String email, String accountNumber);
}
