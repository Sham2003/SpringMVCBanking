// src/main/java/com/example/sample/service/LoanServiceImpl.java
package com.banking.service;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanFeatures;
import com.banking.dto.loan.LoanForm;
import com.banking.ml.LoanApprover;
import com.banking.model.Loan;
import com.banking.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repo;
    private final LoanApprover approver;

    public LoanServiceImpl(LoanRepository repo, LoanApprover approver) {
        this.repo = repo;
        this.approver = approver;
    }

    @Override
    @Transactional
    public Loan processApplication(LoanForm f) throws OrtException {

        // Build feature object
        LoanFeatures features = new LoanFeatures(f);

        String decision = approver.approveLoan(features);

        // Build and save entity
        Loan loan = new Loan(
                f.getLoanId(),
                f.getNoOfDependents(),
                f.getEducation(),
                f.getSelfEmployed(),
                f.getIncomeAnnum(),
                f.getLoanAmount(),
                f.getLoanTerm(),
                f.getCibilScore(),
                f.getResidentialAssetsValue(),
                f.getCommercialAssetsValue(),
                f.getLuxuryAssetsValue(),
                f.getBankAssetValue(),
                decision);

        return repo.save(loan);
    }

    @Override
    public Optional<Loan> findById(String loanId) {
        Loan l1 = repo.findById(loanId).orElse(null);
        return Optional.ofNullable(l1);
    }
}
