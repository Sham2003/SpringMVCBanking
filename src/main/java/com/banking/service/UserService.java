package com.banking.service;
import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.dto.transaction.AccountInfo;
import com.banking.model.User;

import java.security.GeneralSecurityException;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    void saveUser(User user);
    String initiatePasswordReset(String email) ;
    void verifyPasswordReset(ResetPasswordDTO resetPasswordDTO);
    LoginResponse loginCheck(LoginDto loginDto) throws GeneralSecurityException;

    List<AccountInfo> getAccountDetails();

    List<String> getAccountNumbers();
}

