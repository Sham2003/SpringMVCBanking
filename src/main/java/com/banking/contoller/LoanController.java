// src/main/java/com/example/sample/controller/LoanController.java
package com.banking.contoller;

import com.banking.dto.loan.LoanForm;
import com.banking.model.Loan;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;


    /* ------------ 2. HANDLE SUBMIT --------------- */
    @PostMapping("/submitLoanApplication")
    public ResponseEntity<Loan> submitLoanApplication(@RequestBody @Valid LoanForm loanForm,
            BindingResult errors,
            Model model) {
        if (errors.hasErrors()) {
            errors.getFieldErrors()
                  .forEach(fe -> log.warn("Validation error: {} → {}", fe.getField(), fe.getDefaultMessage()));

            throw new ValidationException(errors.toString());
        }

        /* ---- b. business processing ---- */
        Loan loan = loanService.processApplication(loanForm);

        return ResponseEntity.ok().body(loan);
    }

    @GetMapping("/loans")
    public List<String> getLoans(@RequestParam String email){
        return List.of();
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<Loan> showResult(@PathVariable String loanId, Model model) {
        Loan loan =  loanService.findById(loanId)
                               .orElseThrow(() -> new IllegalArgumentException("Unknown loanId " + loanId));
        return ResponseEntity.ok().body(loan);
    }
}
