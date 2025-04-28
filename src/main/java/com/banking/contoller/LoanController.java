// src/main/java/com/example/sample/controller/LoanController.java
package com.banking.contoller;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanForm;
import com.banking.model.Loan;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;


    @PostMapping("/submitLoanApplication")
    public ResponseEntity<String> submitLoanApplication(@RequestBody @Valid LoanForm loanForm,
            BindingResult errors) throws OrtException {
        if (errors.hasErrors()) {
            errors.getFieldErrors()
                  .forEach(fe -> log.warn("Validation error: {} → {}", fe.getField(), fe.getDefaultMessage()));

            throw new ValidationException(errors.toString());
        }

        String loanId = loanService.processApplication(loanForm);

        return ResponseEntity.ok().body(loanId);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<String>> getLoans(@RequestParam String email){
        List<String> loanIds = loanService.getUserLoans(email);
        return ResponseEntity.ok().body(loanIds);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<Loan> showResult(@PathVariable String loanId) {
        Loan loan =  loanService.findById(loanId)
                               .orElseThrow(() -> new IllegalArgumentException("Unknown loanId " + loanId));
        return ResponseEntity.ok().body(loan);
    }
}
