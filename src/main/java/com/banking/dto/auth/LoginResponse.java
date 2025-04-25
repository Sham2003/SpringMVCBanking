package com.banking.dto.auth;

import lombok.Builder;

@Builder
public class LoginResponse {
    private String email;
    private String token;
    private String error;
}
