// src/main/java/com/example/sample/service/LoanServiceImpl.java
package com.banking.service;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanFeatures;
import com.banking.dto.loan.LoanForm;
import com.banking.ml.LoanApprover;
import com.banking.model.Loan;
import com.banking.model.User;
import com.banking.repository.LoanRepository;
import com.banking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanApprover approver;
    private final UserRepository userRepository;

    public LoanServiceImpl(LoanRepository repo, LoanApprover approver, UserRepository userRepository) {
        this.loanRepository = repo;
        this.approver = approver;
        this.userRepository = userRepository;
    }

    public String generateLoanId(){
        return "L" + String.format("%d", new Random().nextInt(1_000_000));
    }

    @Override
    @Transactional
    public String processApplication(LoanForm f) throws OrtException {

        LoanFeatures features = new LoanFeatures(f);

        String decision = approver.approveLoan(features);

        User user = userRepository.findByEmail(f.getEmail());

        Loan loan = Loan.builder()
                .user(user)
                .loanId(generateLoanId())
                .noOfDependents(f.getNoOfDependents())
                .education(f.getEducation())
                .selfEmployed(f.getSelfEmployed())
                .incomeAnnum(f.getIncomeAnnum())
                .loanAmount(f.getLoanAmount())
                .loanTerm(f.getLoanTerm())
                .cibilScore(f.getCibilScore())
                .residentialAssetsValue(f.getResidentialAssetsValue())
                .commercialAssetsValue(f.getCommercialAssetsValue())
                .luxuryAssetsValue(f.getLuxuryAssetsValue())
                .bankAssetValue(f.getBankAssetValue())
                .approvalStatus(decision)
                .build();
        loanRepository.save(loan);
        return loan.getLoanId();
    }

    @Override
    public Optional<Loan> findById(String loanId) {
        Loan l1 = loanRepository.findById(loanId).orElse(null);
        return Optional.ofNullable(l1);
    }

    @Override
    public List<String> getUserLoans(String email) {
        List<String> loans = new ArrayList<>();
        loanRepository.findLoansByUserEmail(email).forEach(loan -> {
            loans.add(loan.getLoanId());
        });
        return loans;
    }
}
