package com.banking.contoller;

import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.RegisterAccountDTO;
import com.banking.dto.auth.RegisterResponse;
import com.banking.exceptions.exps.AuthExceptions.*;
import com.banking.model.Account;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    private boolean isInvalidPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,}$";
        return password == null || !password.matches(pattern);
    }

    @PostMapping(value = "/create-account",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> createAccount(@RequestBody RegisterAccountDTO registerAccountDTO) {
        // Check if the email already exists for an account
        if (accountRepository.findByEmail(registerAccountDTO.getEmail()) != null) {
            throw new AccountExistsException("Account already exists with email: " + registerAccountDTO.getEmail());
        }

        // Validate password and confirm password match
        if (!registerAccountDTO.getPassword().equals(registerAccountDTO.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords do not match");
        }

        // Validate password strength
        if (isInvalidPassword(registerAccountDTO.getPassword())) {
            throw new WeakPasswordException("Password must be at least 6 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        // Check if the user already exists
        if (userService.findByEmail(registerAccountDTO.getEmail()) == null) {
            User newUser = new User();
            newUser.setEmail(registerAccountDTO.getEmail());
            newUser.setPassword(registerAccountDTO.getPassword());
            newUser.setName(registerAccountDTO.getName());
            userService.saveUser(newUser);
        }

        // Validate age (must be at least 18)
        LocalDate dateOfBirth = LocalDate.parse(registerAccountDTO.getDob());
        LocalDate today = LocalDate.now();
        int age = today.getYear() - dateOfBirth.getYear();
        if (dateOfBirth.plusYears(age).isAfter(today)) age--;

        if (age < 18) {
            throw new SecurityException("Age must be at least 18");
        }

        // Generate a random account number
        String accountNumber = "ACC" + String.format("%010d", new Random().nextInt(1_000_000_000));

        // Create and save the new account
        Account account = new Account();
        account.setName(registerAccountDTO.getName());
        account.setEmail(registerAccountDTO.getEmail());
        account.setMobileNumber(registerAccountDTO.getMobileNumber());
        account.setDob(dateOfBirth);
        account.setAccountType(registerAccountDTO.getAccountType());
        account.setAccountNumber(accountNumber);
        account.setBalance(0.0);

        accountRepository.save(account);

        RegisterResponse response = new RegisterResponse(accountNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDto loginDto, Model model) throws AccountLockedException {
        User existingUser = userService.findByEmail(loginDto.getEmail());

        if (existingUser == null) {
            throw new UserNotFoundException("User not found.");
        }

        if (accountRepository.findByEmail(loginDto.getEmail()) == null) {
            throw new AccountNotFoundException("No account exists for this user. Please create an account first.");
        }

        if (existingUser.getAccountLockedUntil() != null &&
                existingUser.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account is locked. Try again after: " + existingUser.getAccountLockedUntil());
        }

        if (!existingUser.getPassword().equals(loginDto.getPassword())) {
            int attempts = existingUser.getFailedLoginAttempts() + 1;
            existingUser.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(3);
                existingUser.setAccountLockedUntil(lockUntil);
                userService.saveUser(existingUser);

                try {
                    userService.sendAccountLockedEmail(existingUser.getEmail(), lockUntil);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                model.addAttribute("message", "Account locked due to 3 failed attempts. Try again after: " + lockUntil);
                throw new AccountLockedException("Account is locked. Try again after: " + lockUntil);
            }

            userService.saveUser(existingUser);
            throw new BadCredentialsException("Invalid credentials! Attempt " + attempts + " of 3.");
        }

        existingUser.setFailedLoginAttempts(0);
        existingUser.setAccountLockedUntil(null);
        userService.saveUser(existingUser);

        return ResponseEntity.status(HttpStatus.OK).body(LoginResponse.builder().email(loginDto.getEmail()).build());
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidPasswordException("Passwords do not match");
        }

        if (isInvalidPassword(newPassword)) {
            throw new WeakPasswordException("Password must be strong (8+ chars, upper, lower, digit, special).");
        }

        User user = userService.findByEmail(email);
        if(user == null) {
            throw new UserNotFoundException("User not found.");
        }
        user.setPassword(newPassword);
        user.setOtp(null);
        userService.saveUser(user);

        return ResponseEntity
                .ok(new Object(){
                    public final String message = "Password reset successful.";
                });
    }
}
