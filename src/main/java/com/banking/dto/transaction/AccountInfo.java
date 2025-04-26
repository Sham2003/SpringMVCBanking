package com.banking.dto.transaction;

import com.banking.model.Account;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class AccountInfo {

    private String name;
    private String email;
    private String mobileNumber;
    private LocalDate dob;
    private String accountType;
    private String accountNumber;
    private String otp;
    private Double balance;
    private LocalDateTime createdOn;

    public AccountInfo(Account account) {
        this.name = account.getName();
        this.email = account.getEmail();
        this.mobileNumber = account.getMobileNumber();
        this.dob = account.getDob();
        this.accountType = account.getAccountType();
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.createdOn = account.getCreatedOn();
    }
}
