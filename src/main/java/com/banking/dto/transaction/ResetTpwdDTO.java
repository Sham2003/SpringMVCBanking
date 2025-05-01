package com.banking.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetTpwdDTO {
    private String accountNumber;
    private String otpReqId;
    private String otp;
    private String transactionPassword;
}
