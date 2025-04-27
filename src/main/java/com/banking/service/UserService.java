package com.banking.service;
import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.model.User;

import javax.security.auth.login.AccountLockedException;

public interface UserService {
    User findByEmail(String email);
    void saveUser(User user);
    String initiatePasswordReset(String email) ;
    void verifyPasswordReset(ResetPasswordDTO resetPasswordDTO);
    LoginResponse loginCheck(LoginDto loginDto) throws AccountLockedException;
}

