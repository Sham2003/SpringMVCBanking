// src/main/java/com/example/sample/service/LoanService.java
package com.banking.service;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanForm;
import com.banking.dto.loan.LoanResponse;

import java.util.List;

public interface LoanService {
    String processApplication(LoanForm form) throws OrtException;

    LoanResponse findById(String loanId);

    List<LoanResponse> getUserLoans();
}