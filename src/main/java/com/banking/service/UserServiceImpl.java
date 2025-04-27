package com.banking.service;

import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.model.User;
import com.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

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
}
