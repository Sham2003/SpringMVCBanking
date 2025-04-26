package com.banking.contoller;

import com.banking.model.User;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Random;

@Controller
public class UserController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;



    // ------------------- SUCCESS PAGE -------------------

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam String accountNumber, Model model) {
        model.addAttribute("accountNumber", accountNumber);
        return "success";
    }



    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }



    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "login";
        }

        model.addAttribute("user", user);
        model.addAttribute("email", user.getEmail());
        return "dashboard";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

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


    @GetMapping("/banktransfer")
    public String showTransactionForm() {
        return "banktransfer";
    }

    @PostMapping("/banktransfer")
public String processTransaction(@RequestParam String senderAccountNumber,
                                 @RequestParam String receiverAccountNumber,
                                 @RequestParam double amount,
                                 Model model) {

    Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber);
    Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber);

    if (receiverAccount == null || senderAccount == null) {
        model.addAttribute("message", "Sender or Receiver account not found.");
        return "banktransfer";
    }

    if (amount <= 0) {
        model.addAttribute("message", "Amount must be greater than 0.");
        return "banktransfer";
    }

    if (senderAccount.getBalance() < amount) {
        model.addAttribute("message", "Insufficient balance.");
        return "banktransfer";
    }

    senderAccount.setBalance(senderAccount.getBalance() - amount);
    receiverAccount.setBalance(receiverAccount.getBalance() + amount);

    accountRepository.save(senderAccount);
    accountRepository.save(receiverAccount);

    // Save transaction records for both sender and receiver
    transactionService.saveTransaction(senderAccountNumber, "transfer", amount, "Transferred to " + receiverAccountNumber);
    transactionService.saveTransaction(receiverAccountNumber, "transfer", amount, "Received from " + senderAccountNumber);

    model.addAttribute("message", "Transaction successful!");
    model.addAttribute("senderBalance", senderAccount.getBalance());
    return "banktransfer";
}

    @GetMapping("/DepoWithdraw")
    public String showDepositWithdrawForm() {
        return "DepoWithdraw";
    }

    @PostMapping("/DepoWithdraw")
public String processDepositWithdraw(@RequestParam String accountNumber,
                                     @RequestParam String transactionType,
                                     @RequestParam double amount,
                                     Model model) {

    Account account = accountRepository.findByAccountNumber(accountNumber);

    if (account == null) {
        model.addAttribute("error", "Account not found.");
        return "DepoWithdraw";
    }

    if (amount <= 0) {
        model.addAttribute("error", "Amount must be greater than 0.");
        return "DepoWithdraw";
    }

    if ("deposit".equals(transactionType)) {
        account.setBalance(account.getBalance() + amount);
        transactionService.saveTransaction(accountNumber, "deposit", amount, "Deposited into account");
    } else if ("withdraw".equals(transactionType)) {
        if (account.getBalance() < amount) {
            model.addAttribute("error", "Insufficient balance.");
            return "DepoWithdraw";
        }
        account.setBalance(account.getBalance() - amount);
        transactionService.saveTransaction(accountNumber, "withdraw", amount, "Withdrawn from account");
    } else {
        model.addAttribute("error", "Invalid transaction type.");
        return "DepoWithdraw";
    }

    accountRepository.save(account);
    model.addAttribute("message", "Transaction successful! New Balance: " + account.getBalance());
    return "DepoWithdraw";
}





    
}