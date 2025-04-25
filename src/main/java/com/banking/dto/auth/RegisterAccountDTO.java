package com.banking.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;


@Data
public class RegisterAccountDTO {
    @NotBlank
    String name;
    @Email
    String email;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @NotBlank
    String mobileNumber;

    @DateTimeFormat
    String dob;


    @NotBlank
    String accountType;

    @NotBlank
    String password;

    @NotBlank
    String confirmPassword;
}
