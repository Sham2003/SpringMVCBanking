package com.banking.service;


import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.model.Account;
import com.banking.model.PendingAccount;

public interface AccountService {
    // Method to get an account by email
    Account getAccountByEmail(String email);
    String createPendingAccount(RegisterAccountDTO newAccount);

    PendingAccount findPendingAccount(String email);

    RegisterResponse verifyPendingAccount(String email, String otpReqId, String otp);

    RegisterResponse registerAccount(RegisterAccountDTO registerAccountDTO);
}
