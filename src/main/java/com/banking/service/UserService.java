package com.banking.service;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.model.User;
import jakarta.mail.MessagingException;

public interface UserService {
    User findByEmail(String email);
    void saveUser(User user);
    String initiatePasswordReset(String email) throws MessagingException;

    void verifyPasswordReset(ResetPasswordDTO resetPasswordDTO);
}

