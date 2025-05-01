// src/main/java/com/example/sample/service/LoanServiceImpl.java
package com.banking.service;

import ai.onnxruntime.OrtException;
import com.banking.dto.loan.LoanFeatures;
import com.banking.dto.loan.LoanForm;
import com.banking.dto.loan.LoanResponse;
import com.banking.exceptions.exps.AuthExceptions;
import com.banking.ml.LoanApprover;
import com.banking.model.Loan;
import com.banking.model.User;
import com.banking.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanApprover approver;


    public String generateLoanId(){
        return "L" + String.format("%d", new Random().nextInt(1_000_000));
    }

    private static User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public String processApplication(LoanForm f) throws OrtException {

        LoanFeatures features = new LoanFeatures(f);

        String decision = approver.approveLoan(features);

        User user = getAuthenticatedUser();
        if(user == null)
            throw new AuthExceptions.UserNotFoundException("User not found");

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
                .createdOn(LocalDateTime.now())
                .build();
        loanRepository.save(loan);
        return loan.getLoanId();
    }

    @Override
    public LoanResponse findById(String loanId) {
        User user = getAuthenticatedUser();
        Loan loan = loanRepository.findLoansByUserEmail(user.getEmail())
                .stream()
                .filter(l -> l.getLoanId().equals(loanId))
                .findFirst()
                .orElseThrow( () -> new AuthExceptions.LoanDetailNotFoundException("Loan details not found with id:" + loanId));
        return new LoanResponse(loan);
    }

    @Override
    public List<LoanResponse> getUserLoans() {
        User user = getAuthenticatedUser();
        if(user == null)
            throw new AuthExceptions.UserNotFoundException("User not found");
        List<LoanResponse> loans = new ArrayList<>();
        loanRepository
                .findLoansByUserEmail(user.getEmail())
                .forEach(loan -> {
                    LoanResponse l1 = new LoanResponse();
                    l1.setLoanId(loan.getLoanId());
                    l1.setLoanAmount(loan.getLoanAmount());
                    loans.add(l1);
                });
        return loans;
    }
}
