package com.banking.service;

import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.dto.transaction.ResetTpwdDTO;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.Account;
import com.banking.model.PendingAccount;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.PendingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final PendingAccountRepository pendingAccountRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<Account> getAccountByEmail(String email) {
        // Fetch the account by email
        return accountRepository.findByEmail(email);
    }

    private static User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
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
        pendingAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));

        pendingAccountRepository.save(pendingAccount);

        return reqId.toString();

    }

    @Override
    public RegisterResponse registerAccount(RegisterAccountDTO registerAccountDTO) {

        if (!getAccountByEmail(registerAccountDTO.getEmail()).isEmpty()) {
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
    public RegisterResponse createAnotherAccount(String accountType) {
        User user = getAuthenticatedUser();
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
        return new RegisterResponse(accountNumber,user.getEmail(),user.getName(),null);
    }



    @Override
    public String initTransactionPassword(String accountNumber){
        User user = getAuthenticatedUser();
        boolean accountExists = user.getAccounts().stream().map(Account::getAccountNumber).anyMatch(accountNumber::equals);
        if (!accountExists) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + accountNumber);
        }
        return otpService.makeTransactionPasswordRequest(user.getEmail(),accountNumber).toString();
    }

    @Override
    public void changeTransactionPassword(ResetTpwdDTO dto) {
        User user = getAuthenticatedUser();
        Optional<Account> account = user.getAccounts()
                .stream()
                .filter(account1 -> account1.getAccountNumber().equals(dto.getAccountNumber()))
                .findFirst();
        if (account.isEmpty()) {
            throw new AuthExceptions.AccountNotFoundException("Account not found : " + dto.getAccountNumber());
        }

        otpService.verifyOtp(user.getEmail(),dto.getOtpReqId(), dto.getOtp());
        account.get().setTransactionPassword(dto.getTransactionPassword());
        accountRepository.save(account.get());
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
        newUser.setValidatedAt(LocalDateTime.now());

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
