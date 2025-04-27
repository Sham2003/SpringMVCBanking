package com.banking.service;

import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.Account;
import com.banking.model.PendingAccount;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.PendingAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    private final PendingAccountRepository pendingAccountRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    // Constructor injection of AccountRepository
    @Autowired
    public AccountServiceImpl(PendingAccountRepository pendingAccountRepository, AccountRepository accountRepository, UserService userService) {
        this.pendingAccountRepository = pendingAccountRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Override
    public Account getAccountByEmail(String email) {
        // Fetch the account by email
        return accountRepository.findByEmail(email);
    }

    public static String generateOtp() {
        return OtpService.generateOtp();
    }

    @Override
    public void createPendingAccount(RegisterAccountDTO newAccount) {
        LocalDate dateOfBirth = LocalDate.parse(newAccount.getDob());
        LocalDate today = LocalDate.now();
        int age = today.getYear() - dateOfBirth.getYear();
        if (dateOfBirth.plusYears(age).isAfter(today)) age--;

        if (age < 18) {
            throw new SecurityException("Age must be at least 18");
        }

        String otp = generateOtp();

        PendingAccount pendingAccount = new PendingAccount();
        pendingAccount.setName(newAccount.getName());
        pendingAccount.setEmail(newAccount.getEmail());
        pendingAccount.setMobileNumber(newAccount.getMobileNumber());
        pendingAccount.setDob(dateOfBirth);
        pendingAccount.setAccountType(newAccount.getAccountType());
        pendingAccount.setOtp(otp);
        pendingAccount.setPassword(newAccount.getPassword());

        pendingAccountRepository.save(pendingAccount);

    }

    @Override
    public PendingAccount findPendingAccount(String email) {
        return pendingAccountRepository.findByEmail(email);
    }

    @Override
    public String verifyPendingAccount(String email) {
        PendingAccount pendingAccount = findPendingAccount(email);
        if (pendingAccount == null) {
            throw new AuthExceptions.AccountNotFoundException("");
        }
        String accountNumber = "ACC" + String.format("%010d", new Random().nextInt(1_000_000_000));
        Account account = pendingAccount.checkout();
        account.setAccountNumber(accountNumber);
        User newUser = new User();
        newUser.setEmail(account.getEmail());
        newUser.setPassword(pendingAccount.getPassword());
        newUser.setName(pendingAccount.getName());
        userService.saveUser(newUser);
        accountRepository.save(account);
        pendingAccountRepository.delete(pendingAccount);
        return accountNumber;
    }
}
