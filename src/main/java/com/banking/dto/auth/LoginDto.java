package com.banking.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto {
    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;
}
