package com.banking.service;

import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String initiatePasswordReset(String email) {
        User user = findByEmail(email);
        if (user == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }
        UUID otpReqId = otpService.makePasswordResetRequest(email);
        return otpReqId.toString();
    }

    private boolean isInvalidPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,}$";
        return password == null || !password.matches(pattern);
    }


    @Override
    public void verifyPasswordReset(ResetPasswordDTO rpDTO) {
        String email = rpDTO.getEmail();
        String newPassword = rpDTO.getNewPassword();
        String confirmPassword = rpDTO.getConfirmPassword();
        otpService.verifyOtp(rpDTO.getEmail(),rpDTO.getReqId(),rpDTO.getOtp());

        if (!newPassword.equals(confirmPassword)) {
            throw new AuthExceptions.InvalidPasswordException("Passwords do not match");
        }

        if (isInvalidPassword(newPassword)) {
            throw new AuthExceptions.WeakPasswordException("Password must be strong (8+ chars, upper, lower, digit, special).");
        }

        User user = findByEmail(email);
        if(user == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }

        user.setPassword(newPassword);
        saveUser(user);
    }

    @Override
    public LoginResponse loginCheck(LoginDto loginDto) throws AccountLockedException {
        User existingUser = findByEmail(loginDto.getEmail());

        if (existingUser == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }

        if (accountRepository.findByEmail(loginDto.getEmail()) == null) {
            throw new AuthExceptions.AccountNotFoundException("No account exists for this user. Please create an account first.");
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
                saveUser(existingUser);

                try {
                    otpService.sendAccountLockedEmail(existingUser.getEmail(), lockUntil);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                throw new AccountLockedException("Account is locked. Try again after: " + lockUntil);
            }

            saveUser(existingUser);
            throw new BadCredentialsException("Invalid credentials! Attempt " + attempts + " of 3.");
        }

        existingUser.setFailedLoginAttempts(0);
        existingUser.setAccountLockedUntil(null);
        saveUser(existingUser);
        return LoginResponse.builder().email(loginDto.getEmail()).build();
    }
}
