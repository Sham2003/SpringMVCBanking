package com.banking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String accountNumber;
    private String email;
    private String name;
    private String otpReqId;
}
