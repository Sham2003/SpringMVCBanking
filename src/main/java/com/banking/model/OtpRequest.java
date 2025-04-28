package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    public enum OTP_TYPE {
        PASSWORD_RESET,
        ACTIVATION_CODE,
        BANK_TRANSFER, TRANSACTION_PASSWORD_CHANGE,
    }

    public enum OTP_STATUS {
        ONGOING,
        EXPIRED,
    }

    private String email;
    private String message;
    private String otp;
    @Enumerated(EnumType.STRING)
    private OTP_TYPE type;

    @Enumerated(EnumType.STRING)
    private OTP_STATUS status;

    private LocalDateTime createdOn = LocalDateTime.now();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
