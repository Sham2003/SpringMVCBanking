package com.banking.config;

import com.banking.exceptions.ErrorCodes;
import com.banking.exceptions.ExceptionResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        System.out.println("JwtAuthenticationEntryPoint triggered");
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        ErrorCodes code = ErrorCodes.MISSING_JWT;

        exceptionResponse.setServerErrorCode(code.getCode());
        exceptionResponse.setServerErrorDescription(code.getDescription());
        exceptionResponse.setError("JWT is missing or invalid");

        response.setStatus(code.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    }
}
