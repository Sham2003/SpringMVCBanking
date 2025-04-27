package com.banking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordDTO {
    private String email;
    private String reqId;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
