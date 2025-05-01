package com.banking.service;


import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.dto.transaction.ResetTpwdDTO;
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

    RegisterResponse createAnotherAccount(String accountType);

    void changeTransactionPassword(ResetTpwdDTO resetPasswordDTO);

    String initTransactionPassword(String accountNumber);
}
