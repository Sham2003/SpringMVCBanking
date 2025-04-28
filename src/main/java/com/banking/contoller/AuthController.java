package com.banking.contoller;

import com.banking.dto.auth.*;
import com.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    @Autowired
    private AccountService accountService;


    @PostMapping(value = "/create-account",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> createAccount(@RequestBody RegisterAccountDTO registerAccountDTO) {
        RegisterResponse response = accountService.registerAccount(registerAccountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-activation-code")
    public ResponseEntity<RegisterResponse> verifyForgotPasswordOtp(@RequestParam("email") String email,
                                          @RequestParam("otpReqId") String otpReqId,
                                          @RequestParam("otp") String otp) {
        RegisterResponse response = accountService.verifyPendingAccount(email,otpReqId,otp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/initTransactionPassword")
    public ResponseEntity<String> initChangeTransactionPassword(@RequestParam("email") String email,
                                                       @RequestParam("accountNumber") String accountNumber) {
        System.out.println("initTransactionPassword");
        String otpReqId = accountService.initTransactionPassword(email,accountNumber);
        return ResponseEntity.ok().body(otpReqId);
    }

    @PostMapping("/changeTransactionPassword")
    public ResponseEntity<?> changeTransactionPassword(@RequestParam("email") String email,
                                                       @RequestParam("accountNumber") String accountNumber,
                                            @RequestParam("otpReqId") String otpReqId,
                                            @RequestParam("otp") String otp,
                                            @RequestParam("transactionPassword") String transactionPassword) {
        accountService.changeTransactionPassword(email,accountNumber,otpReqId,otp,transactionPassword);
        return ResponseEntity.ok().build();
    }



}
