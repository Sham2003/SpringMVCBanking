package com.banking.dto.loan;


import com.banking.model.Loan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LoanResponse {
    private String loanId;
    private int noOfDependents;
    private String education;
    private String selfEmployed;
    private double incomeAnnum;
    private double loanAmount;
    private int loanTerm;
    private int cibilScore;
    private double residentialAssetsValue;
    private double commercialAssetsValue;
    private double luxuryAssetsValue;
    private double bankAssetValue;
    private String approvalStatus;
    private LocalDateTime createdOn;

    public LoanResponse(Loan loan) {
        this.loanId = loan.getLoanId();
        this.noOfDependents = loan.getNoOfDependents();
        this.education = loan.getEducation();
        this.selfEmployed = loan.getSelfEmployed();
        this.incomeAnnum = loan.getIncomeAnnum();
        this.loanAmount = loan.getLoanAmount();
        this.loanTerm = loan.getLoanTerm();
        this.cibilScore = loan.getCibilScore();
        this.residentialAssetsValue = loan.getResidentialAssetsValue();
        this.commercialAssetsValue = loan.getCommercialAssetsValue();
        this.luxuryAssetsValue = loan.getLuxuryAssetsValue();
        this.bankAssetValue = loan.getBankAssetValue();
        this.approvalStatus = loan.getApprovalStatus();
        this.createdOn = loan.getCreatedOn();
    }
}
