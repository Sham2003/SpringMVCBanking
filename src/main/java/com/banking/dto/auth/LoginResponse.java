package com.banking.dto.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String email;
    private String token;
    private String expiresAt;
}
