package com.banking.service;

import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.dto.transaction.AccountInfo;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.Account;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final OtpService otpService;

    private final AccountRepository accountRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AuthExceptions.UserNotFoundException("User not found"));
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

        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);
    }

    @Override
    public LoginResponse loginCheck(LoginDto loginDto) throws GeneralSecurityException {
        User existingUser = findByEmail(loginDto.getEmail());

        if (existingUser == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }
        List<Account> accounts = accountRepository.findByEmail(loginDto.getEmail());

        if (accounts == null || accounts.isEmpty()) {
            throw new AuthExceptions.AccountNotFoundException("No account exists for this user. Please create an account first.");
        }

        if (existingUser.getAccountLockedUntil() != null &&
                existingUser.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account is locked. Try again after: " + existingUser.getAccountLockedUntil());
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), existingUser.getPassword())) {
            int attempts = existingUser.getFailedLoginAttempts() + 1;
            existingUser.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(3);
                existingUser.setAccountLockedUntil(lockUntil);
                saveUser(existingUser);

                try {
                    Map<String,Object> parameters = new HashMap<>();
                    parameters.put("email", existingUser.getEmail());
                    parameters.put("lockUntil", lockUntil);
                    otpService.sendEmail(existingUser.getEmail(), OtpService.EMAIL_TYPE.ACCOUNT_LOCKED,parameters);
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
        jwtService.revokeAllUserTokens(existingUser);


        return jwtService.generateToken(existingUser);
    }



    @Override
    @Transactional
    public List<AccountInfo> getAccountDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        if(user == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }

        Hibernate.initialize(user.getAccounts());

        return user.getAccounts().stream().map(AccountInfo::new).toList();
    }

    @Override
    public List<String> getAccountNumbers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        if(user == null) {
            throw new AuthExceptions.UserNotFoundException("User not found.");
        }

        Hibernate.initialize(user.getAccounts());

        return user.getAccounts().stream().map(Account::getAccountNumber).toList();
    }

}
