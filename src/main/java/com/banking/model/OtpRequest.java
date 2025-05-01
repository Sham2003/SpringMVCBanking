package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;
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
        BANK_TRANSFER,
        TRANSACTION_PASSWORD_CHANGE,
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

    public boolean isExpired(){
        return createdOn.plusMinutes(30).isBefore(LocalDateTime.now()) || getStatus() != OtpRequest.OTP_STATUS.ONGOING;
    }

    public boolean checkOtp(String otp) {
        return this.otp.equals(otp);
    }

    public boolean checkEmail(String email) {
        return this.email.equals(email);
    }

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Always 6 digits
        return String.valueOf(otp);
    }

    public static OtpRequest forEmail(String email, OTP_TYPE type, String message) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.email = email;
        otpRequest.otp = generateOtp();
        otpRequest.message = message;
        otpRequest.type = type;
        otpRequest.status = OtpRequest.OTP_STATUS.ONGOING;
        return otpRequest;
    }
}
