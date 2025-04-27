package com.banking.contoller;

import com.banking.model.User;
import com.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@Controller
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "Email not found.");
            return "forgot-password";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        userService.saveUser(user);

        try {
            userService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            model.addAttribute("message", "Failed to send email.");
            return "forgot-password";
        }

        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyForgotPasswordOtp(@RequestParam("email") String email,
                                          @RequestParam("otp") String otp,
                                          Model model) {
        User user = userService.findByEmail(email);
        if (user == null || !user.getOtp().equals(otp)) {
            model.addAttribute("message", "Invalid OTP.");
            return "verify-otp";
        }

        model.addAttribute("email", email);
        return "reset-password";
    }






    
}