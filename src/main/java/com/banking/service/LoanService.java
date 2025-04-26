// src/main/java/com/example/sample/service/LoanService.java
package com.banking.service;

import com.banking.dto.loan.LoanForm;
import com.banking.model.Loan;

import java.util.Optional;

public interface LoanService {
    Loan processApplication(LoanForm form);

    Optional<Loan> findById(String loanId);
}