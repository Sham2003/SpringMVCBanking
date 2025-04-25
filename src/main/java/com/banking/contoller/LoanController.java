// src/main/java/com/example/sample/controller/LoanController.java
package com.banking.contoller;

import com.banking.dto.LoanForm;
import com.banking.model.Loan;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;

    /* ------------ 1. SHOW BLANK FORM ------------ */
    @GetMapping("/apply")
    public String showForm(Model model) {
        model.addAttribute("loanForm", new LoanForm());
        return "apply";
    }

    /* ------------ 2. HANDLE SUBMIT --------------- */
    @PostMapping("/submitLoanApplication")
    public String submitLoanApplication(
            @ModelAttribute("loanForm") @Valid LoanForm loanForm,
            BindingResult errors,
            Model model) {

        /* ---- a. validation ---- */
        if (errors.hasErrors()) {
            // Log all field errors for debugging
            errors.getFieldErrors()
                  .forEach(fe -> log.warn("Validation error: {} → {}", fe.getField(), fe.getDefaultMessage()));
            // show form again with messages
            return "apply";
        }

        /* ---- b. business processing ---- */
        Loan loan = loanService.processApplication(loanForm);

        // flash attributes if you use RedirectAttributes, but here we'll bind via URL
        return "redirect:/result/" + loan.getLoanId();
    }

    /* ------------ 3. RESULT PAGE --------------- */
    @GetMapping("/result/{loanId}")
    public String showResult(@PathVariable String loanId, Model model) {
        Loan loan =  loanService.findById(loanId)
                               .orElseThrow(() -> new IllegalArgumentException("Unknown loanId " + loanId));
        model.addAttribute("loan", loan);
        return "result";
    }
}
