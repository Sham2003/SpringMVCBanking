package com.banking.contoller;

import com.banking.dto.auth.*;
import com.banking.service.AccountService;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    private final UserService userService;

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> createAccount(@RequestBody RegisterAccountDTO registerAccountDTO) {
        RegisterResponse response = accountService.registerAccount(registerAccountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<RegisterResponse> verifyForgotPasswordOtp(@RequestParam("email") String email,
                                          @RequestParam("otpReqId") String otpReqId,
                                          @RequestParam("otp") String otp) {
        RegisterResponse response = accountService.verifyPendingAccount(email,otpReqId,otp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDto loginDto) throws GeneralSecurityException {
        LoginResponse loginResponse = userService.loginCheck(loginDto);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }


    @PostMapping(value = "/forgot-password",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> initiateResetPassword(@RequestParam("email") String email) {
        String reqId = userService.initiatePasswordReset(email);
        return ResponseEntity.status(HttpStatus.OK).body(reqId);
    }


    @PostMapping(value = "/reset-password",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.verifyPasswordReset(resetPasswordDTO);
        return ResponseEntity
                .ok().build();
    }

    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout() {
    }


}
