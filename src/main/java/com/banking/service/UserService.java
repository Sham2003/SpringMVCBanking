package com.banking.service;
import com.banking.model.User;
import java.time.LocalDateTime;
import jakarta.mail.MessagingException;

public interface UserService {
    User findByEmail(String email);
    void saveUser(User user);
    void sendOtpEmail(String to, String otp) throws MessagingException;

    // For OTP verification during registration
    void storePendingUser(User user);
    User getPendingUser(String email);
    void removePendingUser(String email);
    void sendAccountLockedEmail(String toEmail, LocalDateTime unlockTime);
}

