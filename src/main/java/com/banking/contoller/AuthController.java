package com.banking.contoller;

import com.banking.dto.auth.*;
import com.banking.exceptions.exps.AuthExceptions.*;
import com.banking.model.PendingAccount;
import com.banking.repository.AccountRepository;
import com.banking.service.AccountService;
import com.banking.service.OtpService;
import com.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    private boolean isInvalidPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,}$";
        return password == null || !password.matches(pattern);
    }

    @PostMapping(value = "/create-account",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> createAccount(@RequestBody RegisterAccountDTO registerAccountDTO) {
        // Check if the email already exists for an account
        if (accountService.getAccountByEmail(registerAccountDTO.getEmail()) != null) {
            throw new AccountExistsException("Account already exists with email: " + registerAccountDTO.getEmail());
        }

        if (!registerAccountDTO.getPassword().equals(registerAccountDTO.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords do not match");
        }


        if (isInvalidPassword(registerAccountDTO.getPassword())) {
            throw new WeakPasswordException("Password must be at least 6 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
        accountService.createPendingAccount(registerAccountDTO);

        RegisterResponse response = new RegisterResponse(null,registerAccountDTO.getEmail(),registerAccountDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-activation-code")
    public ResponseEntity<?> verifyForgotPasswordOtp(@RequestParam("email") String email,
                                          @RequestParam("otp") String otp) {
        PendingAccount pendingAccount = accountService.findPendingAccount(email);
        if (pendingAccount == null) {
            throw new AccountNotFoundException("Pending Account not found with email: " + email);
        }
        if(!pendingAccount.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid Activation Code");
        }
        String accountNumber = accountService.verifyPendingAccount(email);
        RegisterResponse response = new RegisterResponse(accountNumber,email,pendingAccount.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
