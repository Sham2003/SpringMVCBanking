// src/main/java/com/example/sample/model/Loan.java
package com.banking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @Column(length = 32)
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

    public Loan() {}  // JPA needs no‑arg ctor

    public Loan(String loanId,
                int noOfDependents,
                String education,
                String selfEmployed,
                double incomeAnnum,
                double loanAmount,
                int loanTerm,
                int cibilScore,
                double residentialAssetsValue,
                double commercialAssetsValue,
                double luxuryAssetsValue,
                double bankAssetValue,
                String approvalStatus) {
        this.loanId = loanId;
        this.noOfDependents = noOfDependents;
        this.education = education;
        this.selfEmployed = selfEmployed;
        this.incomeAnnum = incomeAnnum;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.cibilScore = cibilScore;
        this.residentialAssetsValue = residentialAssetsValue;
        this.commercialAssetsValue = commercialAssetsValue;
        this.luxuryAssetsValue = luxuryAssetsValue;
        this.bankAssetValue = bankAssetValue;
        this.approvalStatus = approvalStatus;
    }

    // ---------- getters & setters ----------
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public int getNoOfDependents() { return noOfDependents; }
    public void setNoOfDependents(int noOfDependents) { this.noOfDependents = noOfDependents; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getSelfEmployed() { return selfEmployed; }
    public void setSelfEmployed(String selfEmployed) { this.selfEmployed = selfEmployed; }

    public double getIncomeAnnum() { return incomeAnnum; }
    public void setIncomeAnnum(double incomeAnnum) { this.incomeAnnum = incomeAnnum; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }

    public int getLoanTerm() { return loanTerm; }
    public void setLoanTerm(int loanTerm) { this.loanTerm = loanTerm; }

    public int getCibilScore() { return cibilScore; }
    public void setCibilScore(int cibilScore) { this.cibilScore = cibilScore; }

    public double getResidentialAssetsValue() { return residentialAssetsValue; }
    public void setResidentialAssetsValue(double residentialAssetsValue) { this.residentialAssetsValue = residentialAssetsValue; }

    public double getCommercialAssetsValue() { return commercialAssetsValue; }
    public void setCommercialAssetsValue(double commercialAssetsValue) { this.commercialAssetsValue = commercialAssetsValue; }

    public double getLuxuryAssetsValue() { return luxuryAssetsValue; }
    public void setLuxuryAssetsValue(double luxuryAssetsValue) { this.luxuryAssetsValue = luxuryAssetsValue; }

    public double getBankAssetValue() { return bankAssetValue; }
    public void setBankAssetValue(double bankAssetValue) { this.bankAssetValue = bankAssetValue; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}
