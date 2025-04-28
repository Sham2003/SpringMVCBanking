package com.banking.service;

import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.Account;
import com.banking.model.PendingAccount;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.PendingAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final PendingAccountRepository pendingAccountRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final OtpService otpService;

    // Constructor injection of AccountRepository
    @Autowired
    public AccountServiceImpl(PendingAccountRepository pendingAccountRepository, AccountRepository accountRepository, UserService userService, OtpService otpService) {
        this.pendingAccountRepository = pendingAccountRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.otpService = otpService;
    }

    @Override
    public Account getAccountByEmail(String email) {
        // Fetch the account by email
        return accountRepository.findByEmail(email);
    }

    private boolean isInvalidPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,}$";
        return password == null || !password.matches(pattern);
    }

    @Override
    public String createPendingAccount(RegisterAccountDTO newAccount) {
        LocalDate dateOfBirth = LocalDate.parse(newAccount.getDob());
        LocalDate today = LocalDate.now();
        int age = today.getYear() - dateOfBirth.getYear();
        if (dateOfBirth.plusYears(age).isAfter(today)) age--;

        if (age < 18) {
            throw new SecurityException("Age must be at least 18");
        }

        UUID reqId = otpService.makeActivationCodeRequest(newAccount.getEmail());
        PendingAccount pendingAccount = new PendingAccount();
        pendingAccount.setName(newAccount.getName());
        pendingAccount.setEmail(newAccount.getEmail());
        pendingAccount.setMobileNumber(newAccount.getMobileNumber());
        pendingAccount.setDob(dateOfBirth);
        pendingAccount.setAccountType(newAccount.getAccountType());
        pendingAccount.setPassword(newAccount.getPassword());

        pendingAccountRepository.save(pendingAccount);

        return reqId.toString();

    }

    @Override
    public RegisterResponse registerAccount(RegisterAccountDTO registerAccountDTO) {

        if (getAccountByEmail(registerAccountDTO.getEmail()) != null) {
            throw new AuthExceptions.AccountExistsException("Account already exists with email: " + registerAccountDTO.getEmail());
        }

        if (!registerAccountDTO.getPassword().equals(registerAccountDTO.getConfirmPassword())) {
            throw new AuthExceptions.InvalidPasswordException("Passwords do not match");
        }


        if (isInvalidPassword(registerAccountDTO.getPassword())) {
            throw new AuthExceptions.WeakPasswordException("Password must be at least 6 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
        String otpReqId = createPendingAccount(registerAccountDTO);

        return new RegisterResponse(null,registerAccountDTO.getEmail(),registerAccountDTO.getName(),otpReqId);
    }

    @Override
    public RegisterResponse createAnotherAccount(String email,String accountType) {
        User user = userService.findByEmail(email);
        if(user == null) {
            throw new AuthExceptions.UserNotFoundException("User not found");
        }
        if(!List.of("savings","current").contains(accountType)) {
            throw new IllegalArgumentException("Account type must be savings or current");
        }
        String accountNumber = "ACC" + String.format("%010d", new Random().nextInt(1_000_000_000));
        Account existingAccount = user.getAccounts().get(0);
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setName(existingAccount.getName());
        account.setEmail(existingAccount.getEmail());
        account.setMobileNumber(existingAccount.getMobileNumber());
        account.setDob(existingAccount.getDob());
        account.setAccountType(accountType);
        account.setBalance(0.0);
        account.setUser(user);

        user.getAccounts().add(account);
        userService.saveUser(user);
        return new RegisterResponse(accountNumber,email,user.getName(),null);
    }

    @Override
    public String initTransactionPassword(String email,String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + accountNumber);
        }
        return otpService.makeTransactionPasswordRequest(email,accountNumber).toString();
    }

    @Override
    public void changeTransactionPassword(String email,String accountNumber, String otpReqId, String otp, String transactionPassword) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + accountNumber);
        }

        otpService.verifyOtp(email,otpReqId,otp);
        account.setTransactionPassword(transactionPassword);
    }

    @Override
    public PendingAccount findPendingAccount(String email) {
        return pendingAccountRepository.findByEmail(email);
    }

    @Override
    public RegisterResponse verifyPendingAccount(String email, String otpReqId, String otp) {
        PendingAccount pendingAccount = findPendingAccount(email);
        if (pendingAccount == null) {
            throw new AuthExceptions.AccountNotFoundException("Pending Account not found with email: " + email);
        }
        otpService.verifyOtp(email,otpReqId,otp);

        String accountNumber = "ACC" + String.format("%010d", new Random().nextInt(1_000_000_000));
        User newUser = new User();
        newUser.setEmail(pendingAccount.getEmail());
        newUser.setPassword(pendingAccount.getPassword());
        newUser.setName(pendingAccount.getName());


        Account account = pendingAccount.checkout();
        account.setAccountNumber(accountNumber);
        account.setUser(newUser);
        newUser.getAccounts().add(account);

        userService.saveUser(newUser);

        //accountRepository.save(account);
        pendingAccountRepository.delete(pendingAccount);

        return new RegisterResponse(accountNumber,email,pendingAccount.getName(), null);
    }

}
