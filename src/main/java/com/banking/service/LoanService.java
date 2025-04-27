// src/main/java/com/example/sample/service/LoanService.java
package com.banking.service;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanForm;
import com.banking.model.Loan;

import java.util.Optional;

public interface LoanService {
    Loan processApplication(LoanForm form) throws OrtException;

    Optional<Loan> findById(String loanId);
}