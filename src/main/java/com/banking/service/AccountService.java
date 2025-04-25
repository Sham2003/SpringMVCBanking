package com.banking.service;


import com.banking.model.Account;

public interface AccountService {
    // Method to get an account by email
    Account getAccountByEmail(String email);
}
