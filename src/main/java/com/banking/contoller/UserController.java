package com.banking.contoller;

import com.banking.dto.auth.LoginDto;
import com.banking.dto.auth.LoginResponse;
import com.banking.dto.auth.ResetPasswordDTO;
import com.banking.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.AccountLockedException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDto loginDto) throws AccountLockedException {
        LoginResponse loginResponse = userService.loginCheck(loginDto);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }


    @PostMapping(value = "/initiatePasswordReset",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> initiateResetPassword(@RequestParam("email") String email) throws MessagingException {
        String reqId = String.valueOf(userService.initiatePasswordReset(email));

        return ResponseEntity.status(HttpStatus.OK).body(reqId);
    }


    @PostMapping(value = "/reset-password",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.verifyPasswordReset(resetPasswordDTO);
        return ResponseEntity
                .ok().build();
    }

    
}