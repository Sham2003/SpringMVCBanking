package com.banking.service;


import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.model.Account;
import com.banking.model.PendingAccount;

public interface AccountService {
    // Method to get an account by email
    Account getAccountByEmail(String email);
    void createPendingAccount(RegisterAccountDTO newAccount);

    PendingAccount findPendingAccount(String email);

    String verifyPendingAccount(String email);
}
